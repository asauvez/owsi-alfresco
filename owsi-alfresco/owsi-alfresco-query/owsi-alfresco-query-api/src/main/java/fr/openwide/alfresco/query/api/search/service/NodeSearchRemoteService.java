package fr.openwide.alfresco.query.api.search.service;

import java.util.List;

import org.springframework.http.HttpMethod;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.api.node.model.NodeReference;
import fr.openwide.alfresco.query.api.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.api.search.model.NodeResult;

public interface NodeSearchRemoteService {

	class GET_NODE_SERVICE {
		public static final String URL = "/owsi/query/node/get";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public NodeReference nodeReference;
		public NodeFetchDetails nodeFetchDetails;
	}
	NodeResult get(NodeReference nodeReference, NodeFetchDetails nodeFetchDetails);

	class SEARCH_NODE_SERVICE {
		public static final String URL = "/owsi/query/node/search";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public String query;
		public NodeFetchDetails nodeFetchDetails;
	}
	List<NodeResult> search(String query, NodeFetchDetails nodeFetchDetails);

	class CHILDREN_NODE_SERVICE {
		public static final String URL = "/owsi/query/node/children";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public NodeReference nodeReference;
		public NameReference childAssocName; 
		public NodeFetchDetails nodeFetchDetails;
	}
	List<NodeResult> getChildren(NodeReference nodeReference, NameReference childAssocName, NodeFetchDetails nodeFetchDetails);

	class TARGET_ASSOC_NODE_SERVICE {
		public static final String URL = "/owsi/query/node/targetassoc";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public NodeReference nodeReference;
		public NameReference assocName; 
		public NodeFetchDetails nodeFetchDetails;
	}
	List<NodeResult> getTargetAssocs(NodeReference nodeReference, NameReference assocName, NodeFetchDetails nodeFetchDetails);

	class SOURCE_ASSOC_NODE_SERVICE {
		public static final String URL = "/owsi/query/node/sourceassoc";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public NodeReference nodeReference;
		public NameReference assocName; 
		public NodeFetchDetails nodeFetchDetails;
	}
	List<NodeResult> getSourceAssocs(NodeReference nodeReference, NameReference assocName, NodeFetchDetails nodeFetchDetails);

}

