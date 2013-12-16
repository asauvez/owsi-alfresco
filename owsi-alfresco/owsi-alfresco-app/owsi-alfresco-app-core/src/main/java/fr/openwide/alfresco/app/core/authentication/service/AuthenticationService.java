package fr.openwide.alfresco.app.core.authentication.service;

import fr.openwide.alfresco.repository.api.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryUser;
import fr.openwide.alfresco.repository.api.authentication.service.AuthenticationRemoteService;
import fr.openwide.core.jpa.exception.SecurityServiceException;

public interface AuthenticationService extends AuthenticationRemoteService {

	/**
	 * Authenticate a pre-authenticated user with its username
	 */
	RepositoryUser authenticate(String username) throws SecurityServiceException;

	/**
	 * Authenticate a pre-authenticated user with its ticket
	 */
	RepositoryUser authenticate(RepositoryTicket ticket) throws SecurityServiceException;

}
