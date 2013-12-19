package fr.openwide.alfresco.repository.remote.search.service.impl;

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

import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.model.RepositoryPermission;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.api.search.service.NodeSearchRemoteService;
import fr.openwide.alfresco.repository.remote.conversion.service.ConversionService;

public class NodeSearchRemoteServiceImpl implements NodeSearchRemoteService {

	private NodeService nodeService;
	private SearchService searchService;
	private PermissionService permissionService;
	private ConversionService conversionService;

	@Override
	public RepositoryNode get(NodeReference nodeReference, NodeFetchDetails details) {
		return getRepositoryNode(conversionService.convert(nodeReference), details);
	}
	
	@Override
	public List<RepositoryNode> search(String luceneQuery, NodeFetchDetails details) {
		ResultSet resultSet = searchService.query(
				StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
				SearchService.LANGUAGE_FTS_ALFRESCO, 
				luceneQuery);
		
		List<RepositoryNode> res = new ArrayList<>();
		for (NodeRef nodeRef : resultSet.getNodeRefs()) {
			res.add(getRepositoryNode(nodeRef, details));
		}
		return res;
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

	private RepositoryNode getRepositoryNode(NodeRef nodeRef, NodeFetchDetails details) {
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
			node.getProperties().put(property, conversionService.convertToApp(value));
		}
		for (NameReference aspect : details.getAspects()) {
			if (nodeService.hasAspect(nodeRef, conversionService.convert(aspect))) {
				node.getAspects().add(aspect);
			}
		}
		for (RepositoryPermission permission : details.getUserPermissions()) {
			if (permissionService.hasPermission(nodeRef, permission.getName()) == AccessStatus.ALLOWED) {
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
