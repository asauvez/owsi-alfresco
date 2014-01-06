package fr.openwide.alfresco.repository.api.node.service;

import java.util.List;

import org.springframework.http.HttpMethod;

import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.exception.NoSuchNodeException;
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
	RepositoryNode get(NodeReference nodeReference, NodeFetchDetails nodeFetchDetails) throws NoSuchNodeException;

	class CHILDREN_NODE_SERVICE {
		public static final String URL = "/owsi/node/children";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public NodeReference nodeReference;
		public NameReference childAssocTypeName; 
		public NodeFetchDetails nodeFetchDetails;
	}
	List<RepositoryNode> getChildren(NodeReference nodeReference, NameReference childAssocTypeName, NodeFetchDetails nodeFetchDetails);

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

	class CREATE_NODE_SERVICE {
		public static final String URL = "/owsi/node";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public RepositoryNode node;
		public String contentBodyProperty;
	}
	NodeReference create(RepositoryNode node) throws DuplicateChildNameException;

	class UPDATE_NODE_SERVICE {
		public static final String URL = "/owsi/node";
		public static final HttpMethod METHOD = HttpMethod.PUT;
		public RepositoryNode node;
		public NodeFetchDetails details;
		public String contentBodyProperty;
	}
	void update(RepositoryNode node, NodeFetchDetails details) throws DuplicateChildNameException;

	interface DELETE_NODE_SERVICE {
		String URL = "/owsi/node";
		HttpMethod METHOD = HttpMethod.DELETE;
	}
	void delete(NodeReference nodeReference);

}
