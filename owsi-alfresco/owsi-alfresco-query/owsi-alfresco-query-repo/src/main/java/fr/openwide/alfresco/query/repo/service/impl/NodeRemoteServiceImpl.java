package fr.openwide.alfresco.query.repo.service.impl;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.api.node.model.NodeReference;
import fr.openwide.alfresco.query.api.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.api.search.model.NodeResult;
import fr.openwide.alfresco.query.repo.service.ConversionService;
import fr.openwide.alfresco.query.repo.service.NodeRemoteService;

public class NodeRemoteServiceImpl implements NodeRemoteService {

	private NodeService nodeService;
	private ConversionService conversionService;

	@Override
	public NodeReference create(NodeResult node) {
		if (node.getNodeReference() != null) {
			throw new IllegalArgumentException("La node est déjà persistée.");
		}
		
		String cmName = (String) node.getProperties().get(conversionService.convert(ContentModel.PROP_NAME));
		
		Map<QName, Serializable> properties = new LinkedHashMap<QName, Serializable>();
		for (Entry<NameReference, Serializable> property : node.getProperties().entrySet()) {
			properties.put(
				conversionService.convert(property.getKey()), 
				conversionService.convertToGed(property.getValue()));
		}
		
		NodeRef nodeRef = nodeService.createNode(
				conversionService.convert(node.getPrimaryParent().getNodeReference()), 
				ContentModel.ASSOC_CONTAINS, 
				QName.createQName(cmName), 
				conversionService.convert(node.getType()), 
				properties).getChildRef();
		
		for (NameReference aspectName : node.getAspects()) {
			nodeService.addAspect(nodeRef, conversionService.convert(aspectName), null);
		}

		return conversionService.convert(nodeRef);
	}

	@Override
	public void update(NodeResult node, NodeFetchDetails details) {
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
				nodeService.setProperty(nodeRef, 
					conversionService.convert(propertyName), 
					conversionService.convertToGed(value));
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
	}
	
	@Override
	public void delete(NodeReference nodeReference) {
		nodeService.deleteNode(conversionService.convert(nodeReference));
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
