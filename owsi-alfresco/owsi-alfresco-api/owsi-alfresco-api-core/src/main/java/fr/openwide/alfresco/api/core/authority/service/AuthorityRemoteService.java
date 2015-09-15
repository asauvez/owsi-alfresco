package fr.openwide.alfresco.api.core.authority.service;

import java.util.List;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthorityQueryParameters;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.endpoint.PostMethodEndpoint;

public interface AuthorityRemoteService {

	class GET_USER {
		public static PostMethodEndpoint<RepositoryNode> ENDPOINT = new PostMethodEndpoint<RepositoryNode>("/owsi/authority/get-user") {};
		public String userName;
		public NodeScope nodeScope;
	}
	RepositoryNode getUser(String userName, NodeScope nodeScope) throws NoSuchNodeRemoteException;

	class GET_CONTAINED_USERS {
		public static PostMethodEndpoint<List<RepositoryNode>> ENDPOINT = new PostMethodEndpoint<List<RepositoryNode>>("/owsi/authority/get-contained-users") {};
		public RepositoryAuthorityQueryParameters searchParameters;
	}
	List<RepositoryNode> getContainedUsers(RepositoryAuthorityQueryParameters searchParameters);

	class GET_CONTAINED_GROUPS {
		public static PostMethodEndpoint<List<RepositoryNode>> ENDPOINT = new PostMethodEndpoint<List<RepositoryNode>>("/owsi/authority/get-contained-groups") {};
		public RepositoryAuthorityQueryParameters searchParameters;
	}
	List<RepositoryNode> getContainedGroups(RepositoryAuthorityQueryParameters searchParameters);
}
