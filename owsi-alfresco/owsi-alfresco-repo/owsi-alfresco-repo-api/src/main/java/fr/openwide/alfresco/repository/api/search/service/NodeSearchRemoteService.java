package fr.openwide.alfresco.repository.api.search.service;

import java.util.List;

import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.model.RemoteCallParameters;
import fr.openwide.alfresco.repository.api.remote.model.StoreReference;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.PostMethodEndpoint;

public interface NodeSearchRemoteService {

	class SEARCH_NODE_SERVICE {
		public static final PostMethodEndpoint<List<RepositoryNode>> ENDPOINT = new PostMethodEndpoint<List<RepositoryNode>>("/owsi/search/node/search") {};
		public String query;
		public StoreReference storeReference;
		public NodeScope nodeScope;
		public RemoteCallParameters remoteCallParameters;
		public SearchQueryLanguage language;
	}

	List<RepositoryNode> search(String query, StoreReference storeReference, 
			NodeScope nodeScope, RemoteCallParameters remoteCallParameters, 
			SearchQueryLanguage language);

}
