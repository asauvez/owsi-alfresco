package fr.openwide.alfresco.api.core.node.service;

import java.util.List;

import fr.openwide.alfresco.api.core.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.model.RemoteCallParameters;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.core.remote.model.endpoint.PostMethodEndpoint;

public interface NodeRemoteService {

	class GET_NODE_SERVICE {
		public static final PostMethodEndpoint<RepositoryNode> ENDPOINT = new PostMethodEndpoint<RepositoryNode>("/owsi/node/get") {};
		public NodeReference nodeReference;
		public NodeScope nodeScope;
		public RemoteCallParameters remoteCallParameters;
	}
	RepositoryNode get(NodeReference nodeReference, NodeScope nodeScope, RemoteCallParameters remoteCallParameters) throws NoSuchNodeRemoteException;

	class CHILDREN_NODE_SERVICE {
		public static final PostMethodEndpoint<List<RepositoryNode>> ENDPOINT = new PostMethodEndpoint<List<RepositoryNode>>("/owsi/node/children") {};
		public NodeReference nodeReference;
		public NameReference childAssocTypeName; 
		public NodeScope nodeScope;
		public RemoteCallParameters remoteCallParameters;
	}
	List<RepositoryNode> getChildren(NodeReference nodeReference, NameReference childAssocTypeName, NodeScope nodeScope, RemoteCallParameters remoteCallParameters);

	class TARGET_ASSOC_NODE_SERVICE {
		public static final PostMethodEndpoint<List<RepositoryNode>> ENDPOINT = new PostMethodEndpoint<List<RepositoryNode>>("/owsi/node/targetassoc") {};
		public NodeReference nodeReference;
		public NameReference assocName; 
		public NodeScope nodeScope;
		public RemoteCallParameters remoteCallParameters;
	}
	List<RepositoryNode> getTargetAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope, RemoteCallParameters remoteCallParameters);

	class SOURCE_ASSOC_NODE_SERVICE {
		public static final PostMethodEndpoint<List<RepositoryNode>> ENDPOINT = new PostMethodEndpoint<List<RepositoryNode>>("/owsi/node/sourceassoc") {};
		public NodeReference nodeReference;
		public NameReference assocName; 
		public NodeScope nodeScope;
		public RemoteCallParameters remoteCallParameters;
	}
	List<RepositoryNode> getSourceAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope, RemoteCallParameters remoteCallParameters);

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
