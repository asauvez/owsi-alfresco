package fr.openwide.alfresco.api.core.authentication.service;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
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
	}
	RepositoryUser authenticate(String username, String password) throws AccessDeniedRemoteException;

	/**
	 * Retrieve user information from a pre-authenticated user
	 */
	GetMethodEndpoint<RepositoryUser> AUTHENTICATED_USER_SERVICE_ENDPOINT = new GetMethodEndpoint<RepositoryUser>("/owsi/authentication/user") {};
	RepositoryUser getAuthenticatedUser();

	/**
	 * Log out an authenticated user
	 */
	PostMethodEndpoint<Void> LOGOUT_SERVICE_ENDPOINT = new PostMethodEndpoint<Void>("/owsi/authentication/ticket/logout") {};
	void logout(RepositoryTicket ticket) throws AccessDeniedRemoteException;

}
