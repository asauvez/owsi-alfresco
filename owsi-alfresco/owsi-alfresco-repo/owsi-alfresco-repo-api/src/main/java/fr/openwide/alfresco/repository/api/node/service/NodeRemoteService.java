package fr.openwide.alfresco.repository.api.node.service;

import java.util.List;

import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.exception.NoSuchNodeException;
import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.DeleteMethodEndpoint;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.GetMethodEndpoint;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.PostMethodEndpoint;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.PutMethodEndpoint;

public interface NodeRemoteService {

	class GET_NODE_SERVICE {
		public static final GetMethodEndpoint<RepositoryNode> ENDPOINT = new GetMethodEndpoint<RepositoryNode>("/owsi/node/get") {};
		public NodeReference nodeReference;
		public NodeScope nodeScope;
	}
	RepositoryNode get(NodeReference nodeReference, NodeScope nodeScope) throws NoSuchNodeException;

	class CHILDREN_NODE_SERVICE {
		public static final GetMethodEndpoint<List<RepositoryNode>> ENDPOINT = new GetMethodEndpoint<List<RepositoryNode>>("/owsi/node/children") {};
		public NodeReference nodeReference;
		public NameReference childAssocTypeName; 
		public NodeScope nodeScope;
	}
	List<RepositoryNode> getChildren(NodeReference nodeReference, NameReference childAssocTypeName, NodeScope nodeScope);

	class TARGET_ASSOC_NODE_SERVICE {
		public static final GetMethodEndpoint<List<RepositoryNode>> ENDPOINT = new GetMethodEndpoint<List<RepositoryNode>>("/owsi/node/targetassoc") {};
		public NodeReference nodeReference;
		public NameReference assocName; 
		public NodeScope nodeScope;
	}
	List<RepositoryNode> getTargetAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope);

	class SOURCE_ASSOC_NODE_SERVICE {
		public static final GetMethodEndpoint<List<RepositoryNode>> ENDPOINT = new GetMethodEndpoint<List<RepositoryNode>>("/owsi/node/sourceassoc") {};
		public NodeReference nodeReference;
		public NameReference assocName; 
		public NodeScope nodeScope;
	}
	List<RepositoryNode> getSourceAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope);

	class CREATE_NODE_SERVICE {
		public static final PostMethodEndpoint<List<NodeReference>> ENDPOINT = new PostMethodEndpoint<List<NodeReference>>("/owsi/node") {};
		public List<RepositoryNode> nodes;
	}
	List<NodeReference> create(List<RepositoryNode> nodes) throws DuplicateChildNameException;

	class UPDATE_NODE_SERVICE {
		public static final PutMethodEndpoint<Void> ENDPOINT = new PutMethodEndpoint<Void>("/owsi/node") {};
		public List<RepositoryNode> nodes;
		public NodeScope nodeScope;
	}
	void update(List<RepositoryNode> nodes, NodeScope nodeScope) throws DuplicateChildNameException;

	DeleteMethodEndpoint<NodeReference> DELETE_NODE_SERVICE_ENDPOINT = new DeleteMethodEndpoint<NodeReference>("/owsi/node") {};
	void delete(NodeReference nodeReference);

}
