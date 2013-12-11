package fr.openwide.alfresco.repository.api.authentication.service;

import org.springframework.http.HttpMethod;

import fr.openwide.alfresco.repository.api.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryUser;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryUserRequest;
import fr.openwide.alfresco.repository.api.remote.model.AccessDeniedRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.RepositoryRemoteException;

public interface AuthenticationRemoteService {

	interface LOGIN_REQUEST_SERVICE {
		String URL = "/owsi/authentication/request";
		HttpMethod METHOD = HttpMethod.POST;
	}

	interface AUTHENTICATED_USER_SERVICE {
		String URL = "/owsi/authentication/user";
		HttpMethod METHOD = HttpMethod.GET;
	}

	interface LOGOUT_SERVICE {
		String URL = "/owsi/authentication/ticket";
		HttpMethod METHOD = HttpMethod.DELETE;
	}

	/**
	 * Authenticate an unknown user
	 */
	RepositoryUser authenticate(RepositoryUserRequest request) throws RepositoryRemoteException;

	/**
	 * Retrieve user information from a pre-authenticated user
	 */
	RepositoryUser getAuthenticatedUser();

	/**
	 * Log out an authenticated user
	 */
	void logout(RepositoryTicket ticket) throws AccessDeniedRemoteException;

}
