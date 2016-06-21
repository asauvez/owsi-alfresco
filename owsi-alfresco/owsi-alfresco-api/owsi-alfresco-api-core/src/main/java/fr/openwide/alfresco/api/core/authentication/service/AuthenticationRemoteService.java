package fr.openwide.alfresco.api.core.authentication.service;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.api.core.authentication.model.UserReference;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.remote.exception.AccessDeniedRemoteException;
import fr.openwide.alfresco.api.core.remote.model.endpoint.GetMethodEndpoint;
import fr.openwide.alfresco.api.core.remote.model.endpoint.PostMethodEndpoint;

public interface AuthenticationRemoteService {

	/**
	 * Authenticate an unknown user
	 */
	class LOGIN_REQUEST_SERVICE {
		public static final PostMethodEndpoint<RepositoryUser> ENDPOINT = new PostMethodEndpoint<RepositoryUser>("/owsi/authentication/request") {};
		public String username;
		public String password;
		public NodeScope nodeScope;
	}
	RepositoryUser authenticate(String username, String password, NodeScope nodeScope) throws AccessDeniedRemoteException;

	/**
	 * Retrieve user information from a pre-authenticated user
	 */
	class AUTHENTICATED_USER_SERVICE { 
		public static final PostMethodEndpoint<RepositoryUser> ENDPOINT = new PostMethodEndpoint<RepositoryUser>("/owsi/authentication/user") {}; 
		public NodeScope nodeScope; 
	} 
	RepositoryUser getAuthenticatedUser(NodeScope nodeScope); 

	/** Retreive just the username. Used by @see SlingshotApiController in Share */
	GetMethodEndpoint<UserReference> GET_AUTHENTICATED_USERNAME_ENDPOINT = new GetMethodEndpoint<UserReference>("/owsi/authentication/username") {};
	String getAuthenticatedUsername(); 
	
	/**
	 * Log out an authenticated user
	 */
	PostMethodEndpoint<Void> LOGOUT_SERVICE_ENDPOINT = new PostMethodEndpoint<Void>("/owsi/authentication/ticket/logout") {};
	void logout(RepositoryTicket ticket) throws AccessDeniedRemoteException;

}
