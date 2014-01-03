package fr.openwide.alfresco.app.core.search.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.app.core.search.service.NodeSearchService;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.StoreReference;

@Service
public class NodeSearchServiceImpl implements NodeSearchService {

	@Autowired
	private RepositoryRemoteBinding repositoryRemoteBinding;

	@Override
	public List<RepositoryNode> search(String query, StoreReference storeReference, NodeFetchDetails nodeFetchDetails) {
		try {
			SEARCH_NODE_SERVICE request = new SEARCH_NODE_SERVICE();
			request.query = query;
			request.storeReference = storeReference;
			request.nodeFetchDetails = nodeFetchDetails;
			return repositoryRemoteBinding.exchangeCollection(SEARCH_NODE_SERVICE.URL, 
					SEARCH_NODE_SERVICE.METHOD, request, new ParameterizedTypeReference<List<RepositoryNode>>() {});
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

}
