package fr.openwide.alfresco.api.core.authority.service;

import java.util.List;

import fr.openwide.alfresco.api.core.authority.exception.AuthorityExistsRemoteException;
import fr.openwide.alfresco.api.core.authority.model.AuthorityQueryParameters;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.wsgenerator.annotation.GenerateWebScript.WebScriptMethod;
import fr.openwide.alfresco.repository.wsgenerator.annotation.WebScriptEndPoint;
import fr.openwide.alfresco.repository.wsgenerator.model.WebScriptParam;

public interface AuthorityRemoteService {

	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/authority/get-user")
	class GET_USER extends WebScriptParam<RepositoryNode> {
		public String userName;
		public NodeScope nodeScope;
	}
	RepositoryNode getUser(String userName, NodeScope nodeScope) throws NoSuchNodeRemoteException;

	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/authority/create-user")
	class CREATE_USER extends WebScriptParam<RepositoryNode> {
		public String userName;
		public String firstName;
		public String lastName;
		public String email;
		public String password;
		public NodeScope nodeScope;
	}
	RepositoryNode createUser(String userName, String firstName, String lastName, String email, String password, NodeScope nodeScope) throws AuthorityExistsRemoteException;

	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/authority/delete-user")
	class DELETE_USER extends WebScriptParam<Void> {
		public String userName;
	}
	void deleteUser(String userName) throws NoSuchNodeRemoteException;

	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/authority/update-user-password")
	class UPDATE_USER_PASSWORD extends WebScriptParam<Void> {
		public String userName;
		public String newPassword;
	}
	void updateUserPassword(String userName, String newPassword) throws NoSuchNodeRemoteException;

	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/authority/get-contained-authorities")
	class GET_CONTAINED_AUTHORITIES extends WebScriptParam<List<RepositoryNode>> {
		public AuthorityQueryParameters searchParameters;
	}
	List<RepositoryNode> getContainedAuthorities(AuthorityQueryParameters searchParameters);

	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/authority/get-group")
	class GET_GROUP extends WebScriptParam<RepositoryNode> {
		public String groupShortName;
		public NodeScope nodeScope;
	}
	RepositoryNode getGroup(String groupShortName, NodeScope nodeScope) throws NoSuchNodeRemoteException;

	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/authority/create-root-group")
	class CREATE_ROOT_GROUP extends WebScriptParam<RepositoryNode> {
		public String groupShortName;
		public String groupDisplayName;
		public NodeScope nodeScope;
	}
	RepositoryNode createRootGroup(String groupShortName, String groupDisplayName, NodeScope nodeScope) throws AuthorityExistsRemoteException;

	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/authority/delete-group")
	class DELETE_GROUP extends WebScriptParam<Void> {
		public String groupShortName;
	}
	void deleteGroup(String groupShortName) throws NoSuchNodeRemoteException;

	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/authority/add-to-group")
	class ADD_TO_GROUP extends WebScriptParam<Void> {
		public String subAuthorityFullName;
		public String parentGroupShortName;
	}
	void addToGroup(String subAuthorityFullName, String parentGroupShortName) throws NoSuchNodeRemoteException;

	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/authority/remove-from-group")
	class REMOVE_FROM_GROUP extends WebScriptParam<Void> {
		public String subAuthorityFullName;
		public String parentGroupShortName;
	}
	void removeFromGroup(String subAuthorityFullName, String parentGroupShortName) throws NoSuchNodeRemoteException;

}
