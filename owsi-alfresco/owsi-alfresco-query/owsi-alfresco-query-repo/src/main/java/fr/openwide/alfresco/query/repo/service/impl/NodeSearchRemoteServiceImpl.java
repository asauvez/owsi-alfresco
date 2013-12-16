package fr.openwide.alfresco.query.repo.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.RegexQNamePattern;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.api.node.model.NodeReference;
import fr.openwide.alfresco.query.api.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.api.search.model.NodeResult;
import fr.openwide.alfresco.query.api.search.service.NodeSearchRemoteService;
import fr.openwide.alfresco.query.repo.service.ConversionService;

public class NodeSearchRemoteServiceImpl implements NodeSearchRemoteService {

	private NodeService nodeService;
	private SearchService searchService;
	private PermissionService permissionService;
	private ConversionService conversionService;

	@Override
	public NodeResult get(NodeReference nodeReference, NodeFetchDetails details) {
		return getNodeResult(conversionService.convert(nodeReference), details);
	}
	
	@Override
	public List<NodeResult> search(String luceneQuery, NodeFetchDetails details) {
		ResultSet resultSet = searchService.query(
				StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
				SearchService.LANGUAGE_FTS_ALFRESCO, 
				luceneQuery);
		
		List<NodeResult> res = new ArrayList<>();
		for (NodeRef nodeRef : resultSet.getNodeRefs()) {
			res.add(getNodeResult(nodeRef, details));
		}
		return res;
	}

	@Override
	public List<NodeResult> getChildren(
			NodeReference nodeReference, NameReference childAssocName, NodeFetchDetails details) {
		List<ChildAssociationRef> assocs = nodeService.getChildAssocs(
				conversionService.convert(nodeReference), 
				conversionService.convert(childAssocName), 
				RegexQNamePattern.MATCH_ALL, true);
		
		List<NodeResult> res = new ArrayList<>();
		for (ChildAssociationRef assoc : assocs) {
			res.add(getNodeResult(assoc.getChildRef(), details));
		}
		return res;
	}

	@Override
	public List<NodeResult> getTargetAssocs(
			NodeReference nodeReference, NameReference assocName, NodeFetchDetails details) {
		List<AssociationRef> assocs = nodeService.getTargetAssocs(
				conversionService.convert(nodeReference), 
				conversionService.convert(assocName));
		
		List<NodeResult> res = new ArrayList<>();
		for (AssociationRef assoc : assocs) {
			res.add(getNodeResult(assoc.getTargetRef(), details));
		}
		return res;
	}

	@Override
	public List<NodeResult> getSourceAssocs(
			NodeReference nodeReference, NameReference assocName, NodeFetchDetails details) {
		List<AssociationRef> assocs = nodeService.getSourceAssocs(
				conversionService.convert(nodeReference), 
				conversionService.convert(assocName));
		
		List<NodeResult> res = new ArrayList<>();
		for (AssociationRef assoc : assocs) {
			res.add(getNodeResult(assoc.getSourceRef(), details));
		}
		return res;
	}

	private NodeResult getNodeResult(NodeRef nodeRef, NodeFetchDetails details) {
		NodeResult node = new NodeResult();
		if (details.isNodeReference()) {
			node.setNodeReference(conversionService.convert(nodeRef));
		}
		if (details.isType()) {
			node.setType(conversionService.convert(nodeService.getType(nodeRef)));
		}
		if (details.getPrimaryParent() != null) {
			ChildAssociationRef primaryParent = nodeService.getPrimaryParent(nodeRef);
			node.setPrimaryParent(getNodeResult(primaryParent.getParentRef(), details.getPrimaryParent()));
		}
		for (NameReference property : details.getProperties()) {
			Serializable value = nodeService.getProperty(nodeRef, conversionService.convert(property));
			node.getProperties().put(property, conversionService.convertToApp(value));
		}
		for (NameReference aspect : details.getAspects()) {
			if (nodeService.hasAspect(nodeRef, conversionService.convert(aspect))) {
				node.getAspects().add(aspect);
			}
		}
		for (String permission : details.getUserPermissions()) {
			if (permissionService.hasPermission(nodeRef, permission) == AccessStatus.ALLOWED) {
				node.getUserPermissions().add(permission);
			}
		}
		return node;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
}
