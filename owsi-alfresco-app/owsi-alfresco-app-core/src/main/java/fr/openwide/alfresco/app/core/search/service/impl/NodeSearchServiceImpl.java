package fr.openwide.alfresco.app.core.search.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.search.model.RepositorySearchParameters;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.app.core.search.service.NodeSearchService;

public class NodeSearchServiceImpl implements NodeSearchService {

	private static final Logger LOGGER = LoggerFactory.getLogger(NodeSearchServiceImpl.class);
	
	private final RepositoryRemoteBinding repositoryRemoteBinding;

	public NodeSearchServiceImpl(RepositoryRemoteBinding repositoryRemoteBinding) {
		this.repositoryRemoteBinding = repositoryRemoteBinding;
	}

	@Override
	public List<RepositoryNode> search(String query, NodeScope nodeScope) {
		RepositorySearchParameters searchParameters = new RepositorySearchParameters();
		searchParameters.setQuery(query);
		return search(searchParameters, nodeScope);
	}

	@Override
	public List<RepositoryNode> search(RepositorySearchParameters searchParameters, NodeScope nodeScope) {
		LOGGER.debug(searchParameters.getQuery().replace("\n", " "));
		
		SEARCH_NODE_SERVICE payload = new SEARCH_NODE_SERVICE();
		payload.searchParameters = searchParameters;
		payload.nodeScope = nodeScope;
		return repositoryRemoteBinding.callNodeListSerializer(payload, nodeScope);
	}

}
