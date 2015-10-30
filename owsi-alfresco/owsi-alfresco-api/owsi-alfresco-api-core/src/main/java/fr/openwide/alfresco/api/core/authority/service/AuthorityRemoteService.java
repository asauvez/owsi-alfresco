package fr.openwide.alfresco.api.core.authority.service;

import java.util.List;

import fr.openwide.alfresco.api.core.authority.exception.AuthorityExistsRemoteException;
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

	class CREATE_USER {
		public static PostMethodEndpoint<RepositoryNode> ENDPOINT = new PostMethodEndpoint<RepositoryNode>("/owsi/authority/create-user") {};
		public String userName;
		public String firstName;
		public String lastName;
		public String email;
		public String password;
		public NodeScope nodeScope;
	}
	RepositoryNode createUser(String userName, String firstName, String lastName, String email, String password, NodeScope nodeScope) throws AuthorityExistsRemoteException;

	class DELETE_USER {
		public static PostMethodEndpoint<Void> ENDPOINT = new PostMethodEndpoint<Void>("/owsi/authority/delete-user") {};
		public String userName;
	}
	void deleteUser(String userName) throws NoSuchNodeRemoteException;

	class UPDATE_USER_PASSWORD {
		public static PostMethodEndpoint<Void> ENDPOINT = new PostMethodEndpoint<Void>("/owsi/authority/update-user-password") {};
		public String userName;
		public String newPassword;
	}
	void updateUserPassword(String userName, String newPassword) throws NoSuchNodeRemoteException;

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

	class GET_GROUP {
		public static PostMethodEndpoint<RepositoryNode> ENDPOINT = new PostMethodEndpoint<RepositoryNode>("/owsi/authority/get-group") {};
		public String groupShortName;
		public NodeScope nodeScope;
	}
	RepositoryNode getGroup(String groupShortName, NodeScope nodeScope) throws NoSuchNodeRemoteException;

	class CREATE_ROOT_GROUP {
		public static PostMethodEndpoint<RepositoryNode> ENDPOINT = new PostMethodEndpoint<RepositoryNode>("/owsi/authority/create-root-group") {};
		public String groupShortName;
		public String groupDisplayName;
		public NodeScope nodeScope;
	}
	RepositoryNode createRootGroup(String groupShortName, String groupDisplayName, NodeScope nodeScope) throws AuthorityExistsRemoteException;

	class DELETE_GROUP {
		public static PostMethodEndpoint<Void> ENDPOINT = new PostMethodEndpoint<Void>("/owsi/authority/delete-group") {};
		public String groupShortName;
	}
	void deleteGroup(String groupShortName) throws NoSuchNodeRemoteException;

	class ADD_TO_GROUP {
		public static PostMethodEndpoint<Void> ENDPOINT = new PostMethodEndpoint<Void>("/owsi/authority/add-to-group") {};
		public String subAuthorityFullName;
		public String parentGroupShortName;
	}
	void addToGroup(String subAuthorityFullName, String parentGroupShortName) throws NoSuchNodeRemoteException;

	class REMOVE_FROM_GROUP {
		public static PostMethodEndpoint<Void> ENDPOINT = new PostMethodEndpoint<Void>("/owsi/authority/remove-from-group") {};
		public String subAuthorityFullName;
		public String parentGroupShortName;
	}
	void removeFromGroup(String subAuthorityFullName, String parentGroupShortName) throws NoSuchNodeRemoteException;

}
