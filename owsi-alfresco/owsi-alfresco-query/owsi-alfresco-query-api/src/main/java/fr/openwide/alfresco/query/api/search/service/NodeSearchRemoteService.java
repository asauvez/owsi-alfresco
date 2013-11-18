package fr.openwide.alfresco.query.api.search.service;

import java.util.List;

import org.springframework.http.HttpMethod;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.api.node.model.NodeReference;
import fr.openwide.alfresco.query.api.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.api.search.model.NodeResult;


public interface NodeSearchRemoteService {

	interface GET_NODE_SERVICE {
		String URL = "owsi/query/node/get";
		HttpMethod METHOD = HttpMethod.GET;
		String PARAMETER_NODE_REFERENCE = "nodeReference";
		String PARAMETER_NODE_FETCH_DETAIL = "nodeFetchDetails";
	}
	NodeResult get(NodeReference nodeReference, NodeFetchDetails nodeFetchDetails);

	interface SEARCH_NODE_SERVICE {
		String URL = "owsi/query/node/search";
		HttpMethod METHOD = HttpMethod.GET;
		String PARAMETER_QUERY = "query";
		String PARAMETER_NODE_FETCH_DETAIL = "nodeFetchDetails";
	}
	List<NodeResult> search(String query, NodeFetchDetails nodeFetchDetails);

	interface CHILDREN_NODE_SERVICE {
		String URL = "owsi/query/node/children";
		HttpMethod METHOD = HttpMethod.GET;
		String PARAMETER_NODE_REFERENCE = "nodeReference";
		String PARAMETER_CHILD_ASSOC_NAME = "childAssocName";
		String PARAMETER_NODE_FETCH_DETAIL = "nodeFetchDetails";
	}
	List<NodeResult> getChildren(NodeReference nodeReference, NameReference childAssocName, NodeFetchDetails nodeFetchDetails);

	interface TARGET_ASSOC_NODE_SERVICE {
		String URL = "owsi/query/node/targetassoc";
		HttpMethod METHOD = HttpMethod.GET;
		String PARAMETER_NODE_REFERENCE = "nodeReference";
		String PARAMETER_ASSOC_NAME = "assocName";
		String PARAMETER_NODE_FETCH_DETAIL = "nodeFetchDetails";
	}
	List<NodeResult> getTargetAssocs(NodeReference nodeReference, NameReference assocName, NodeFetchDetails nodeFetchDetails);

	interface SOURCE_ASSOC_NODE_SERVICE {
		String URL = "owsi/query/node/sourceassoc";
		HttpMethod METHOD = HttpMethod.GET;
		String PARAMETER_NODE_REFERENCE = "nodeReference";
		String PARAMETER_ASSOC_NAME = "assocName";
		String PARAMETER_NODE_FETCH_DETAIL = "nodeFetchDetails";
	}
	List<NodeResult> getSourceAssocs(NodeReference nodeReference, NameReference assocName, NodeFetchDetails nodeFetchDetails);

}

