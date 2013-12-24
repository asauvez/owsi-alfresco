package fr.openwide.alfresco.repository.api.node.service;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;

import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public interface NodeRemoteService {

	class GET_NODE_SERVICE {
		public static final String URL = "/owsi/node/get";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public NodeReference nodeReference;
		public NodeFetchDetails nodeFetchDetails;
	}
	RepositoryNode get(NodeReference nodeReference, NodeFetchDetails nodeFetchDetails);

	class CHILDREN_NODE_SERVICE {
		public static final String URL = "/owsi/node/children";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public NodeReference nodeReference;
		public NameReference childAssocName; 
		public NodeFetchDetails nodeFetchDetails;
	}
	List<RepositoryNode> getChildren(NodeReference nodeReference, NameReference childAssocName, NodeFetchDetails nodeFetchDetails);

	class TARGET_ASSOC_NODE_SERVICE {
		public static final String URL = "/owsi/node/targetassoc";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public NodeReference nodeReference;
		public NameReference assocName; 
		public NodeFetchDetails nodeFetchDetails;
	}
	List<RepositoryNode> getTargetAssocs(NodeReference nodeReference, NameReference assocName, NodeFetchDetails nodeFetchDetails);

	class SOURCE_ASSOC_NODE_SERVICE {
		public static final String URL = "/owsi/node/sourceassoc";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public NodeReference nodeReference;
		public NameReference assocName; 
		public NodeFetchDetails nodeFetchDetails;
	}
	List<RepositoryNode> getSourceAssocs(NodeReference nodeReference, NameReference assocName, NodeFetchDetails nodeFetchDetails);

	interface CREATE_NODE_SERVICE {
		String URL = "/owsi/node";
		HttpMethod METHOD = HttpMethod.POST;
	}
	NodeReference create(RepositoryNode node, Resource content) throws DuplicateChildNameException;

	class UPDATE_NODE_SERVICE {
		public static final String URL = "/owsi/node";
		public static final HttpMethod METHOD = HttpMethod.PUT;
		public RepositoryNode node;
		public NodeFetchDetails details;
	}
	void update(RepositoryNode node, NodeFetchDetails details, Resource content);

	interface DELETE_NODE_SERVICE {
		String URL = "/owsi/node";
		HttpMethod METHOD = HttpMethod.DELETE;
	}
	void delete(NodeReference nodeReference);

}
