package fr.openwide.alfresco.repository.remote.node.service.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.DuplicateChildNodeNameException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.springframework.core.io.Resource;

import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.model.RepositoryPermission;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.remote.conversion.service.ConversionService;

public class NodeRemoteServiceImpl implements NodeRemoteService {

	private NodeService nodeService;
	private ContentService contentService;
	private PermissionService permissionService;

	private ConversionService conversionService;

	@Override
	public RepositoryNode get(NodeReference nodeReference, NodeFetchDetails details) {
		return getRepositoryNode(conversionService.convert(nodeReference), details);
	}

	@Override
	public List<RepositoryNode> getChildren(
			NodeReference nodeReference, NameReference childAssocName, NodeFetchDetails details) {
		List<ChildAssociationRef> assocs = nodeService.getChildAssocs(
				conversionService.convert(nodeReference), 
				conversionService.convert(childAssocName), 
				RegexQNamePattern.MATCH_ALL, true);
		
		List<RepositoryNode> res = new ArrayList<>();
		for (ChildAssociationRef assoc : assocs) {
			res.add(getRepositoryNode(assoc.getChildRef(), details));
		}
		return res;
	}

	@Override
	public List<RepositoryNode> getTargetAssocs(
			NodeReference nodeReference, NameReference assocName, NodeFetchDetails details) {
		List<AssociationRef> assocs = nodeService.getTargetAssocs(
				conversionService.convert(nodeReference), 
				conversionService.convert(assocName));
		
		List<RepositoryNode> res = new ArrayList<>();
		for (AssociationRef assoc : assocs) {
			res.add(getRepositoryNode(assoc.getTargetRef(), details));
		}
		return res;
	}

	@Override
	public List<RepositoryNode> getSourceAssocs(
			NodeReference nodeReference, NameReference assocName, NodeFetchDetails details) {
		List<AssociationRef> assocs = nodeService.getSourceAssocs(
				conversionService.convert(nodeReference), 
				conversionService.convert(assocName));
		
		List<RepositoryNode> res = new ArrayList<>();
		for (AssociationRef assoc : assocs) {
			res.add(getRepositoryNode(assoc.getSourceRef(), details));
		}
		return res;
	}

	public RepositoryNode getRepositoryNode(NodeRef nodeRef, NodeFetchDetails details) {
		NodeReference nodeReference = conversionService.convert(nodeRef);
		
		RepositoryNode node = new RepositoryNode();
		if (details.isNodeReference()) {
			node.setNodeReference(conversionService.convert(nodeRef));
		}
		if (details.isType()) {
			node.setType(conversionService.convert(nodeService.getType(nodeRef)));
		}
		if (details.getPrimaryParent() != null) {
			ChildAssociationRef primaryParent = nodeService.getPrimaryParent(nodeRef);
			node.setPrimaryParent(getRepositoryNode(primaryParent.getParentRef(), details.getPrimaryParent()));
		}
		for (NameReference property : details.getProperties()) {
			Serializable value = nodeService.getProperty(nodeRef, conversionService.convert(property));
			if (value != null) {
				node.getProperties().put(property, conversionService.convertToApp(value));
			}
		}
		for (NameReference property : details.getContentStrings()) {
			ContentReader reader = contentService.getReader(nodeRef, conversionService.convert(property));
			if (reader != null) {
				String content = reader.getContentString();
				node.getContentStrings().put(property, content);
			}
		}
		for (NameReference aspect : details.getAspects()) {
			if (nodeService.hasAspect(nodeRef, conversionService.convert(aspect))) {
				node.getAspects().add(aspect);
			}
		}
		
		for (Entry<NameReference, NodeFetchDetails> entry : details.getChildAssociations().entrySet()) {
			node.getChildAssociations().put(
					entry.getKey(), 
					getChildren(nodeReference, entry.getKey(), entry.getValue()));
		}
		for (Entry<NameReference, NodeFetchDetails> entry : details.getTargetAssocs().entrySet()) {
			node.getTargetAssocs().put(
					entry.getKey(), 
					getTargetAssocs(nodeReference, entry.getKey(), entry.getValue()));
		}
		for (Entry<NameReference, NodeFetchDetails> entry : details.getSourceAssocs().entrySet()) {
			node.getSourceAssocs().put(
					entry.getKey(), 
					getSourceAssocs(nodeReference, entry.getKey(), entry.getValue()));
		}

		for (RepositoryPermission permission : details.getUserPermissions()) {
			if (permissionService.hasPermission(nodeRef, permission.getName()) == AccessStatus.ALLOWED) {
				node.getUserPermissions().add(permission);
			}
		}
		return node;
	}
	
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

		NameReference type = node.getType();
		if (type == null) {
			throw new IllegalArgumentException("Vous devez fournir le type du noeud.");
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
					conversionService.convert(type), 
					properties).getChildRef();
			
			for (NameReference aspectName : node.getAspects()) {
				nodeService.addAspect(nodeRef, conversionService.convert(aspectName), null);
			}
	
			setContents(nodeRef, node, content);
	
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
		
		if (details.getPrimaryParent() != null) {
			NodeReference parentRef = node.getPrimaryParent().getNodeReference();
			if (parentRef == null) {
				throw new IllegalArgumentException("Vous devez fournir le nodeRef du noeud parent.");
			}
			String cmName = (String) node.getProperties().get(conversionService.convert(ContentModel.PROP_NAME));
			if (cmName == null) {
				throw new IllegalArgumentException("Vous devez fournir un cm:name.");
			}			
			nodeService.moveNode(nodeRef, conversionService.convert(parentRef), ContentModel.ASSOC_CONTAINS, 
					QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, cmName.toLowerCase()));
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
		
		for (Entry<NameReference, NodeFetchDetails> entry : details.getChildAssociations().entrySet()) {
			for (RepositoryNode childNode : node.getChildAssociations().get(entry.getKey())) {
				childNode.setPrimaryParent(node);
				saveOrUpdate(childNode, entry.getValue());
			}
		}
		
		setContents(nodeRef, node, content);
	}

	private void saveOrUpdate(RepositoryNode node, NodeFetchDetails details) {
		if (node.getNodeReference() == null) {
			try {
				create(node, null);
			} catch (DuplicateChildNameException e) {
				throw new IllegalArgumentException(e);
			}
		} else {
			update(node, details, null);
		}
	}
	
	private void setContents(NodeRef nodeRef, RepositoryNode node, Resource contentResource) {
		for (Entry<NameReference, String> entry : node.getContentStrings().entrySet()) {
			RepositoryContentData contentData = (RepositoryContentData) node.getProperties().remove(entry.getKey());
			setContent(nodeRef, entry.getKey(), 
					(contentData != null) ? contentData : new RepositoryContentData(), 
					null, entry.getValue());
		}
		
		for (Entry<NameReference, Serializable> entry : node.getProperties().entrySet()) {
			Serializable value = entry.getValue();
			if (value instanceof RepositoryContentData) {
				if (contentResource == null) {
					throw new IllegalArgumentException("Une resource n'a pas été fourni : " + entry.getKey());
				}
				setContent(nodeRef, entry.getKey(), (RepositoryContentData) value, contentResource, null);
			}
		}
	}
	
	private void setContent(NodeRef nodeRef, NameReference propertyName, RepositoryContentData contentData, 
			Resource contentResource, String contentString) {
		QName qname = conversionService.convert(propertyName);
		ContentWriter writer = contentService.getWriter(nodeRef, qname, true);

		if (contentData.getMimetype() != null) {
			writer.setMimetype(contentData.getMimetype());
		}
		if (contentData.getEncoding() != null) {
			writer.setEncoding(contentData.getEncoding());
		}
		if (contentData.getLocale() != null) {
			writer.setLocale(contentData.getLocale());
		}

		if (contentString != null) {
			writer.putContent(contentString);
		} else {
			try {
				writer.putContent(contentResource.getInputStream());
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
		
		if (contentData.getMimetype() == null || contentData.getEncoding() == null) {
			if (contentData.getMimetype() == null) {
				String cmName = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
				writer.guessMimetype(cmName);
			}
			if (contentData.getEncoding() == null) {
				writer.guessEncoding();
			}
			nodeService.setProperty(nodeRef, qname, writer.getContentData());
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
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
