package fr.openwide.alfresco.repository.api.search.service;

import java.util.List;

import org.springframework.http.HttpMethod;

import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public interface NodeSearchRemoteService {

	/**
	 * @return null si la node n'existe pas.
	 */
	class GET_NODE_SERVICE {
		public static final String URL = "/owsi/search/node/get";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public NodeReference nodeReference;
		public NodeFetchDetails nodeFetchDetails;
	}
	RepositoryNode get(NodeReference nodeReference, NodeFetchDetails nodeFetchDetails);

	class SEARCH_NODE_SERVICE {
		public static final String URL = "/owsi/search/node/search";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public String query;
		public NodeFetchDetails nodeFetchDetails;
	}
	List<RepositoryNode> search(String query, NodeFetchDetails nodeFetchDetails);

	class CHILDREN_NODE_SERVICE {
		public static final String URL = "/owsi/search/node/children";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public NodeReference nodeReference;
		public NameReference childAssocName; 
		public NodeFetchDetails nodeFetchDetails;
	}
	List<RepositoryNode> getChildren(NodeReference nodeReference, NameReference childAssocName, NodeFetchDetails nodeFetchDetails);

	class TARGET_ASSOC_NODE_SERVICE {
		public static final String URL = "/owsi/search/node/targetassoc";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public NodeReference nodeReference;
		public NameReference assocName; 
		public NodeFetchDetails nodeFetchDetails;
	}
	List<RepositoryNode> getTargetAssocs(NodeReference nodeReference, NameReference assocName, NodeFetchDetails nodeFetchDetails);

	class SOURCE_ASSOC_NODE_SERVICE {
		public static final String URL = "/owsi/search/node/sourceassoc";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public NodeReference nodeReference;
		public NameReference assocName; 
		public NodeFetchDetails nodeFetchDetails;
	}
	List<RepositoryNode> getSourceAssocs(NodeReference nodeReference, NameReference assocName, NodeFetchDetails nodeFetchDetails);

}

