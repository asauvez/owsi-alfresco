package fr.openwide.alfresco.repository.api.node.service;

import java.util.List;

import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.repository.api.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.PostMethodEndpoint;

public interface NodeRemoteService {

	class GET_NODE_SERVICE {
		public static final PostMethodEndpoint<RepositoryNode> ENDPOINT = new PostMethodEndpoint<RepositoryNode>("/owsi/node/get") {};
		public NodeReference nodeReference;
		public NodeScope nodeScope;
	}
	RepositoryNode get(NodeReference nodeReference, NodeScope nodeScope) throws NoSuchNodeRemoteException;

	class CHILDREN_NODE_SERVICE {
		public static final PostMethodEndpoint<List<RepositoryNode>> ENDPOINT = new PostMethodEndpoint<List<RepositoryNode>>("/owsi/node/children") {};
		public NodeReference nodeReference;
		public NameReference childAssocTypeName; 
		public NodeScope nodeScope;
	}
	List<RepositoryNode> getChildren(NodeReference nodeReference, NameReference childAssocTypeName, NodeScope nodeScope);

	class TARGET_ASSOC_NODE_SERVICE {
		public static final PostMethodEndpoint<List<RepositoryNode>> ENDPOINT = new PostMethodEndpoint<List<RepositoryNode>>("/owsi/node/targetassoc") {};
		public NodeReference nodeReference;
		public NameReference assocName; 
		public NodeScope nodeScope;
	}
	List<RepositoryNode> getTargetAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope);

	class SOURCE_ASSOC_NODE_SERVICE {
		public static final PostMethodEndpoint<List<RepositoryNode>> ENDPOINT = new PostMethodEndpoint<List<RepositoryNode>>("/owsi/node/sourceassoc") {};
		public NodeReference nodeReference;
		public NameReference assocName; 
		public NodeScope nodeScope;
	}
	List<RepositoryNode> getSourceAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope);

	class CREATE_NODE_SERVICE {
		public static final PostMethodEndpoint<List<NodeReference>> ENDPOINT = new PostMethodEndpoint<List<NodeReference>>("/owsi/node/create") {};
		public List<RepositoryNode> nodes;
	}
	List<NodeReference> create(List<RepositoryNode> nodes) throws DuplicateChildNodeNameRemoteException;

	class UPDATE_NODE_SERVICE {
		public static final PostMethodEndpoint<Void> ENDPOINT = new PostMethodEndpoint<Void>("/owsi/node/update") {};
		public List<RepositoryNode> nodes;
		public NodeScope nodeScope;
	}
	void update(List<RepositoryNode> nodes, NodeScope nodeScope) throws DuplicateChildNodeNameRemoteException;

	class DELETE_NODE_SERVICE {
		public static final PostMethodEndpoint<Void> ENDPOINT = new PostMethodEndpoint<Void>("/owsi/node/delete") {};
		public List<NodeReference> nodeReferences;
	}
	void delete(List<NodeReference> nodeReferences);

}
