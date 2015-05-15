package fr.openwide.alfresco.api.core.authority.service;

import java.util.List;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RemoteCallParameters;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.endpoint.PostMethodEndpoint;

public interface AuthorityRemoteService {

	class GET_CONTAINED_USERS {
		public static PostMethodEndpoint<List<RepositoryNode>> ENDPOINT = new PostMethodEndpoint<List<RepositoryNode>>("/owsi/authority/get-contained-users") {};
		public RepositoryAuthority authority;
		public boolean immediate;
		public NodeScope nodeScope;
		public RemoteCallParameters remoteCallParameters;
	}
	List<RepositoryNode> getContainedUsers(RepositoryAuthority authority, boolean immediate, NodeScope nodeScope, RemoteCallParameters remoteCallParameters);

	class GET_CONTAINED_GROUPS {
		public static PostMethodEndpoint<List<RepositoryNode>> ENDPOINT = new PostMethodEndpoint<List<RepositoryNode>>("/owsi/authority/get-contained-groups") {};
		public RepositoryAuthority authority;
		public boolean immediate;
		public NodeScope nodeScope;
		public RemoteCallParameters remoteCallParameters;
	}
	List<RepositoryNode> getContainedGroups(RepositoryAuthority authority, boolean immediate, NodeScope nodeScope, RemoteCallParameters remoteCallParameters);
}
