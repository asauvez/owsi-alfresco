package fr.openwide.alfresco.app.core.search.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.app.core.search.service.NodeSearchService;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.StoreReference;

@Service
public class NodeSearchServiceImpl implements NodeSearchService {

	@Autowired
	private RepositoryRemoteBinding repositoryRemoteBinding;

	@Override
	public List<RepositoryNode> search(String query, StoreReference storeReference, NodeFetchDetails nodeFetchDetails) {
		SEARCH_NODE_SERVICE request = new SEARCH_NODE_SERVICE();
		request.query = query;
		request.storeReference = storeReference;
		request.nodeFetchDetails = nodeFetchDetails;
		
		return repositoryRemoteBinding.builder(SEARCH_NODE_SERVICE.ENDPOINT)
				.headerPayload(request)
				.call();
	}

}
