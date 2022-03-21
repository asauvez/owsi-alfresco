package fr.openwide.alfresco.app.core.authentication.service;

import fr.openwide.alfresco.api.core.authentication.model.TicketReference;
import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.remote.exception.AccessDeniedRemoteException;

public interface AuthenticationService extends AuthenticationRemoteService {

	RepositoryUser authenticate(String username, String password) throws AccessDeniedRemoteException;
	
	/**
	 * Authenticate a pre-authenticated user with its username
	 */
	RepositoryUser authenticate(String username) throws AccessDeniedRemoteException;
	RepositoryUser authenticate(String username, NodeScope nodeScope) throws AccessDeniedRemoteException;

	/**
	 * Authenticate a pre-authenticated user with its ticket
	 */
	RepositoryUser authenticate(TicketReference ticket) throws AccessDeniedRemoteException;
	RepositoryUser authenticate(TicketReference ticket, NodeScope nodeScope) throws AccessDeniedRemoteException;
	
	NodeScope getDefaultUserNodeScope();

}
