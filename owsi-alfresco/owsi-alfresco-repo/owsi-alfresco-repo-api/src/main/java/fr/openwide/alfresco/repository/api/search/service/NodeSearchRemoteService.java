package fr.openwide.alfresco.repository.api.search.service;

import java.util.List;

import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.StoreReference;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.GetMethodEndpoint;

public interface NodeSearchRemoteService {

	class SEARCH_NODE_SERVICE {
		public static final GetMethodEndpoint<List<RepositoryNode>> ENDPOINT = new GetMethodEndpoint<List<RepositoryNode>>("/owsi/search/node/search") {};
		public String query;
		public StoreReference storeReference;
		public NodeScope nodeScope;
	}
	List<RepositoryNode> search(String query, StoreReference storeReference, NodeScope nodeScope);

}
