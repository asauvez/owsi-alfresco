package fr.openwide.alfresco.api.core.search.service;

import java.util.List;

import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.StoreReference;
import fr.openwide.alfresco.api.core.remote.model.endpoint.PostMethodEndpoint;
import fr.openwide.alfresco.api.core.search.model.SearchQueryLanguage;

public interface NodeSearchRemoteService {

	class SEARCH_NODE_SERVICE {
		public static final PostMethodEndpoint<List<RepositoryNode>> ENDPOINT = new PostMethodEndpoint<List<RepositoryNode>>("/owsi/search/node/search") {};
		public String query;
		public StoreReference storeReference;
		public NodeScope nodeScope;
		public SearchQueryLanguage language;
	}

	List<RepositoryNode> search(
			String query, 
			StoreReference storeReference, 
			NodeScope nodeScope, 
			SearchQueryLanguage language);

}
