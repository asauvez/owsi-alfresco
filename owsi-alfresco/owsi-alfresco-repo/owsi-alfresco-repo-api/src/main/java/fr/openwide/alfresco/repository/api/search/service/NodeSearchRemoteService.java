package fr.openwide.alfresco.repository.api.search.service;

import java.util.List;

import org.springframework.http.HttpMethod;

import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.StoreReference;

public interface NodeSearchRemoteService {

	class SEARCH_NODE_SERVICE {
		public static final String URL = "/owsi/search/node/search";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public String query;
		public StoreReference storeReference;
		public NodeFetchDetails nodeFetchDetails;
	}
	List<RepositoryNode> search(String query, StoreReference storeReference, NodeFetchDetails nodeFetchDetails);

}
