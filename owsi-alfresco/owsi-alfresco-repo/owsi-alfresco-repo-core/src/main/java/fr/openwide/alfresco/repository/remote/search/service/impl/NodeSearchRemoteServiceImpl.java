package fr.openwide.alfresco.repository.remote.search.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;

import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.search.service.NodeSearchRemoteService;
import fr.openwide.alfresco.repository.remote.node.service.impl.NodeRemoteServiceImpl;

public class NodeSearchRemoteServiceImpl implements NodeSearchRemoteService {

	private NodeRemoteServiceImpl nodeRemoteService;
	private SearchService searchService;
	
	@Override
	public List<RepositoryNode> search(String luceneQuery, NodeFetchDetails details) {
		ResultSet resultSet = searchService.query(
				StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
				SearchService.LANGUAGE_FTS_ALFRESCO, 
				luceneQuery);
		
		List<RepositoryNode> res = new ArrayList<>();
		for (NodeRef nodeRef : resultSet.getNodeRefs()) {
			res.add(nodeRemoteService.getRepositoryNode(nodeRef, details));
		}
		return res;
	}

	public void setNodeRemoteService(NodeRemoteServiceImpl nodeRemoteService) {
		this.nodeRemoteService = nodeRemoteService;
	}
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
}
