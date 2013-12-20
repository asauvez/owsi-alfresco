package fr.openwide.alfresco.repository.remote.node.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.core.io.Resource;

import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.remote.conversion.service.ConversionService;

public class NodeRemoteServiceImpl implements NodeRemoteService {

	private NodeService nodeService;
	private ContentService contentService;
	private ConversionService conversionService;

	@Override
	public NodeReference create(RepositoryNode node, Resource content) throws DuplicateChildNameException {
		if (node.getNodeReference() != null) {
			throw new IllegalArgumentException("La node est déjà persistée.");
		}
		
		String cmName = (String) node.getProperties().get(conversionService.convert(ContentModel.PROP_NAME));
		if (cmName == null) {
			throw new IllegalArgumentException("Vous devez fournir un cm:name.");
		}
		
		NodeReference parentRef = node.getPrimaryParent().getNodeReference();
		if (parentRef == null) {
			throw new IllegalArgumentException("Vous devez fournir le nodeRef du noeud parent.");
		}
		
		Map<QName, Serializable> properties = new LinkedHashMap<QName, Serializable>();
		for (Entry<NameReference, Serializable> property : node.getProperties().entrySet()) {
			Serializable value = property.getValue();
			if (! (value instanceof RepositoryContentData)) {
				properties.put(
					conversionService.convert(property.getKey()), 
					conversionService.convertToGed(value));
			}
		}
		
		try {
			NodeRef nodeRef = nodeService.createNode(
					 conversionService.convert(parentRef), 
					ContentModel.ASSOC_CONTAINS, 
					QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, cmName.toLowerCase()), 
					conversionService.convert(node.getType()), 
					properties).getChildRef();
			
			for (NameReference aspectName : node.getAspects()) {
				nodeService.addAspect(nodeRef, conversionService.convert(aspectName), null);
			}
	
			for (Entry<NameReference, Serializable> property : node.getProperties().entrySet()) {
				Serializable value = property.getValue();
				if (value instanceof RepositoryContentData) {
					setContent(nodeRef, property.getKey(), (RepositoryContentData) value, content);
				}
			}
	
			return conversionService.convert(nodeRef);
		} catch (DuplicateChildNodeNameException e) {
			throw new DuplicateChildNameException(e);
		}
	}

	@Override
	public void update(RepositoryNode node, NodeFetchDetails details, Resource content) {
		if (node.getNodeReference() == null) {
			throw new IllegalArgumentException("La node n'est pas persistée.");
		}
		NodeRef nodeRef = conversionService.convert(node.getNodeReference());
		
		if (details.isType()) {
			nodeService.setType(nodeRef, conversionService.convert(node.getType()));
		}
		
		for (NameReference propertyName : details.getProperties()) {
			Serializable value = node.getProperties().get(propertyName);
			if (value != null) {
				if (! (value instanceof ContentData)) {
					nodeService.setProperty(nodeRef, 
						conversionService.convert(propertyName), 
						conversionService.convertToGed(value));
				}
			} else {
				nodeService.removeProperty(nodeRef, 
					conversionService.convert(propertyName)); 
			}
		}
		
		for (NameReference aspectName : details.getAspects()) {
			if (node.getAspects().contains(aspectName)) {
				nodeService.addAspect(nodeRef, conversionService.convert(aspectName), null);
			} else {
				nodeService.removeAspect(nodeRef, conversionService.convert(aspectName));
			}
		}
		
		for (Entry<NameReference, Serializable> property : node.getProperties().entrySet()) {
			Serializable value = property.getValue();
			if (value instanceof RepositoryContentData) {
				setContent(nodeRef, property.getKey(), (RepositoryContentData) value, content);
			}
		}
	}

	private void setContent(NodeRef nodeRef, NameReference propertyName, RepositoryContentData contentData, Resource content) {
		ContentWriter writer = contentService.getWriter(nodeRef, 
				conversionService.convert(propertyName), 
				false);
		try {
			writer.putContent(content.getInputStream());
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		
		if (contentData.getMimetype() != null) {
			writer.setMimetype(contentData.getMimetype());
		} else {
			String cmName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
			writer.guessMimetype(cmName);
		}
		if (contentData.getEncoding() != null) {
			writer.setEncoding(contentData.getEncoding());
		} else {
			writer.guessEncoding();
		}
		if (contentData.getLocale() != null) {
			writer.setLocale(contentData.getLocale());
		}
	}


	@Override
	public void delete(NodeReference nodeReference) {
		nodeService.deleteNode(conversionService.convert(nodeReference));
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
