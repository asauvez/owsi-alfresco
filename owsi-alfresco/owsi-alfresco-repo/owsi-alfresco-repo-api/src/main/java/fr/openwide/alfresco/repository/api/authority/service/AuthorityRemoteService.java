package fr.openwide.alfresco.repository.api.authority.service;

import java.util.List;

import org.springframework.http.HttpMethod;

import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryAuthority;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;

public interface AuthorityRemoteService {

	class GET_CONTAINED_USERS {
		public static final String URL = "/owsi/authority/get-contained-users";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public RepositoryAuthority authority;
		public boolean immediate;
		public NodeFetchDetails nodeFetchDetails;
	}
	List<RepositoryNode> getContainedUsers(RepositoryAuthority authority, boolean immediate, NodeFetchDetails nodeFetchDetails);

	class GET_CONTAINED_GROUPS {
		public static final String URL = "/owsi/authority/get-contained-groups";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public RepositoryAuthority authority;
		public boolean immediate;
		public NodeFetchDetails nodeFetchDetails;
	}
	List<RepositoryNode> getContainedGroups(RepositoryAuthority authority, boolean immediate, NodeFetchDetails nodeFetchDetails);
}
