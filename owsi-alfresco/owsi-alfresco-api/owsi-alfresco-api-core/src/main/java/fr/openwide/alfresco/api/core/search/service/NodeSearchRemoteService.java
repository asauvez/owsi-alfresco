package fr.openwide.alfresco.api.core.search.service;

import java.util.List;

import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.endpoint.PostMethodEndpoint;
import fr.openwide.alfresco.api.core.search.model.RepositorySearchParameters;

public interface NodeSearchRemoteService {

	class SEARCH_NODE_SERVICE {
		public static final PostMethodEndpoint<List<RepositoryNode>> ENDPOINT = new PostMethodEndpoint<List<RepositoryNode>>("/owsi/search/node/search") {};
		public RepositorySearchParameters searchParameters;
		public NodeScope nodeScope;
	}
	List<RepositoryNode> search(RepositorySearchParameters searchParameters, NodeScope nodeScope);

}
