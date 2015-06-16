package fr.openwide.alfresco.app.core.authentication.service.impl;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.remote.exception.AccessDeniedRemoteException;
import fr.openwide.alfresco.app.core.authentication.service.AuthenticationService;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;

public class AuthenticationServiceImpl implements AuthenticationService {

	private final RepositoryRemoteBinding unauthenticatedRepositoryRemoteBinding;
	private final RepositoryRemoteBinding requiringExplicitTicketRemoteBinding;
	private final RepositoryRemoteBinding authenticationRemoteBinding;

	private final String authenticationHeader;
	private final NodeScope defaultUserNodeScope;
	
	public AuthenticationServiceImpl(RepositoryRemoteBinding unauthenticatedRepositoryRemoteBinding,
			RepositoryRemoteBinding requiringExplicitTicketRemoteBinding,
			RepositoryRemoteBinding authenticationRemoteBinding, String authenticationHeader) {
		this.unauthenticatedRepositoryRemoteBinding = unauthenticatedRepositoryRemoteBinding;
		this.requiringExplicitTicketRemoteBinding = requiringExplicitTicketRemoteBinding;
		this.authenticationRemoteBinding = authenticationRemoteBinding;
		this.authenticationHeader = authenticationHeader;

		defaultUserNodeScope = new NodeScope();
		defaultUserNodeScope.getProperties().add(RepositoryUser.FIRST_NAME);
		defaultUserNodeScope.getProperties().add(RepositoryUser.LAST_NAME);
		defaultUserNodeScope.getProperties().add(RepositoryUser.EMAIL);
	}

	@Override
	public RepositoryUser authenticate(String username) throws AccessDeniedRemoteException {
		return authenticationRemoteBinding.builder(AUTHENTICATED_USER_SERVICE.ENDPOINT)
				.header(authenticationHeader, username)
				.call();
	}

	@Override
	public RepositoryUser authenticate(RepositoryTicket ticket) throws AccessDeniedRemoteException {
		return requiringExplicitTicketRemoteBinding.builder(AUTHENTICATED_USER_SERVICE.ENDPOINT)
				.urlVariable(ticket)
				.call();
	}
	
	@Override
	public RepositoryUser authenticate(String username, String password) throws AccessDeniedRemoteException {
		return authenticate(username, password, defaultUserNodeScope);
	}

	@Override
	public RepositoryUser authenticate(String username, String password, NodeScope nodeScope) throws AccessDeniedRemoteException {
		LOGIN_REQUEST_SERVICE request = new LOGIN_REQUEST_SERVICE();
		request.username = username;
		request.password = password;
		request.nodeScope = nodeScope;
		
		return unauthenticatedRepositoryRemoteBinding.builder(LOGIN_REQUEST_SERVICE.ENDPOINT, request)
				.call();
	}

	@Override
	public RepositoryUser getAuthenticatedUser() {
		return getAuthenticatedUser(getDefaultUserNodeScope());
	}
	
	@Override
	public RepositoryUser getAuthenticatedUser(NodeScope nodeScope) {
		throw new UnsupportedOperationException("Use authenticate methods");
	}

	@Override
	public void logout(RepositoryTicket ticket) throws AccessDeniedRemoteException {
		requiringExplicitTicketRemoteBinding.builder(LOGOUT_SERVICE_ENDPOINT, ticket)
			.urlVariable(ticket)
			.call();
	}

	@Override
	public NodeScope getDefaultUserNodeScope() {
		return defaultUserNodeScope;
	}
}
