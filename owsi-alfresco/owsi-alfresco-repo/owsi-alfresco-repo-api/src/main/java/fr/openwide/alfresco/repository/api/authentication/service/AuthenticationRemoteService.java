package fr.openwide.alfresco.repository.api.authentication.service;

import org.springframework.http.HttpMethod;

import fr.openwide.alfresco.repository.api.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryUser;
import fr.openwide.alfresco.repository.api.remote.exception.AccessDeniedRemoteException;

public interface AuthenticationRemoteService {

	/**
	 * Authenticate an unknown user
	 */
	class LOGIN_REQUEST_SERVICE {
		public static final String URL = "/owsi/authentication/request";
		public static final HttpMethod METHOD = HttpMethod.POST;
		public String username;
		public String password;
	}
	RepositoryUser authenticate(String username, String password) throws AccessDeniedRemoteException;

	/**
	 * Retrieve user information from a pre-authenticated user
	 */
	interface AUTHENTICATED_USER_SERVICE {
		String URL = "/owsi/authentication/user";
		HttpMethod METHOD = HttpMethod.GET;
	}
	RepositoryUser getAuthenticatedUser();

	/**
	 * Log out an authenticated user
	 */
	interface LOGOUT_SERVICE {
		String URL = "/owsi/authentication/ticket";
		HttpMethod METHOD = HttpMethod.DELETE;
	}
	void logout(RepositoryTicket ticket) throws AccessDeniedRemoteException;

}
