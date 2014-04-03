package fr.openwide.alfresco.repository.api.authority.service;

import java.util.List;

import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryAuthority;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.GetMethodEndpoint;

public interface AuthorityRemoteService {

	class GET_CONTAINED_USERS {
		public static GetMethodEndpoint<List<RepositoryNode>> ENDPOINT = new GetMethodEndpoint<List<RepositoryNode>>("/owsi/authority/get-contained-users") {};
		public RepositoryAuthority authority;
		public boolean immediate;
		public NodeScope nodeScope;
	}
	List<RepositoryNode> getContainedUsers(RepositoryAuthority authority, boolean immediate, NodeScope nodeScope);

	class GET_CONTAINED_GROUPS {
		public static GetMethodEndpoint<List<RepositoryNode>> ENDPOINT = new GetMethodEndpoint<List<RepositoryNode>>("/owsi/authority/get-contained-groups") {};
		public RepositoryAuthority authority;
		public boolean immediate;
		public NodeScope nodeScope;
	}
	List<RepositoryNode> getContainedGroups(RepositoryAuthority authority, boolean immediate, NodeScope nodeScope);
}
