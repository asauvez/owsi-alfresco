package fr.openwide.alfresco.api.core.authentication.service;

import fr.openwide.alfresco.api.core.authentication.model.TicketReference;
import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.remote.exception.AccessDeniedRemoteException;
import fr.openwide.alfresco.repository.wsgenerator.annotation.GenerateWebScript.WebScriptMethod;
import fr.openwide.alfresco.repository.wsgenerator.annotation.WebScriptEndPoint;
import fr.openwide.alfresco.repository.wsgenerator.model.WebScriptParam;

public interface AuthenticationRemoteService {

	/**
	 * Authenticate an unknown user
	 */
	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/authentication/request")
	class LOGIN_REQUEST_SERVICE extends WebScriptParam<RepositoryUser> {
		public String username;
		public String password;
		public NodeScope nodeScope;
	}
	RepositoryUser authenticate(String username, String password, NodeScope nodeScope) throws AccessDeniedRemoteException;

	/**
	 * Retrieve user information from a pre-authenticated user
	 */
	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/authentication/user")
	class AUTHENTICATED_USER_SERVICE extends WebScriptParam<RepositoryUser> { 
		public NodeScope nodeScope; 
	} 
	RepositoryUser getAuthenticatedUser(NodeScope nodeScope); 

	/** Retreive just the username. Used by @see SlingshotApiController in Share */
	@WebScriptEndPoint(method=WebScriptMethod.GET, url="/owsi/authentication/username")
	class AUTHENTICATED_USERNAME_SERVICE extends WebScriptParam<String> {} 
	String getAuthenticatedUsername(); 
	
	/**
	 * Log out an authenticated user
	 */
	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/authentication/ticket/logout")
	class LOGOUT_SERVICE extends WebScriptParam<Void> {} 
	void logout(TicketReference ticket) throws AccessDeniedRemoteException;

}
