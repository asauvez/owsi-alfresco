package fr.openwide.alfresco.repository.api.node.service;

import java.util.List;

import org.springframework.http.HttpMethod;

import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.exception.NoSuchNodeException;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.DeleteMethodEndpoint;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.GetMethodEndpoint;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.PostMethodEndpoint;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.PutMethodEndpoint;

public interface NodeRemoteService {

	class GET_NODE_SERVICE {
		public static GetMethodEndpoint<RepositoryNode> ENDPOINT = new GetMethodEndpoint<RepositoryNode>("/owsi/node/get") {};
		public NodeReference nodeReference;
		public NodeFetchDetails nodeFetchDetails;
	}
	RepositoryNode get(NodeReference nodeReference, NodeFetchDetails nodeFetchDetails) throws NoSuchNodeException;

	class CHILDREN_NODE_SERVICE {
		public static GetMethodEndpoint<List<RepositoryNode>> ENDPOINT = new GetMethodEndpoint<List<RepositoryNode>>("/owsi/node/children") {};
		public static final HttpMethod METHOD = HttpMethod.GET;
		public NodeReference nodeReference;
		public NameReference childAssocTypeName; 
		public NodeFetchDetails nodeFetchDetails;
	}
	List<RepositoryNode> getChildren(NodeReference nodeReference, NameReference childAssocTypeName, NodeFetchDetails nodeFetchDetails);

	class TARGET_ASSOC_NODE_SERVICE {
		public static GetMethodEndpoint<List<RepositoryNode>> ENDPOINT = new GetMethodEndpoint<List<RepositoryNode>>("/owsi/node/targetassoc") {};
		public NodeReference nodeReference;
		public NameReference assocName; 
		public NodeFetchDetails nodeFetchDetails;
	}
	List<RepositoryNode> getTargetAssocs(NodeReference nodeReference, NameReference assocName, NodeFetchDetails nodeFetchDetails);

	class SOURCE_ASSOC_NODE_SERVICE {
		public static GetMethodEndpoint<List<RepositoryNode>> ENDPOINT = new GetMethodEndpoint<List<RepositoryNode>>("/owsi/node/sourceassoc") {};
		public NodeReference nodeReference;
		public NameReference assocName; 
		public NodeFetchDetails nodeFetchDetails;
	}
	List<RepositoryNode> getSourceAssocs(NodeReference nodeReference, NameReference assocName, NodeFetchDetails nodeFetchDetails);

	class CREATE_NODE_SERVICE {
		public static PostMethodEndpoint<NodeReference> ENDPOINT = new PostMethodEndpoint<NodeReference>("/owsi/node") {};
		public RepositoryNode node;
		public String contentBodyProperty;
	}
	NodeReference create(RepositoryNode node) throws DuplicateChildNameException;

	class UPDATE_NODE_SERVICE {
		public static PutMethodEndpoint<Void> ENDPOINT = new PutMethodEndpoint<Void>("/owsi/node") {};
		public RepositoryNode node;
		public NodeFetchDetails details;
		public String contentBodyProperty;
	}
	void update(RepositoryNode node, NodeFetchDetails details) throws DuplicateChildNameException;

	DeleteMethodEndpoint<NodeReference> DELETE_NODE_SERVICE_ENDPOINT = new DeleteMethodEndpoint<NodeReference>("/owsi/node") {};
	void delete(NodeReference nodeReference);

}
