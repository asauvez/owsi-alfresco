package fr.openwide.alfresco.app.core.authentication.service.impl;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.api.core.remote.exception.AccessDeniedRemoteException;
import fr.openwide.alfresco.app.core.authentication.service.AuthenticationService;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;

public class AuthenticationServiceImpl implements AuthenticationService {

	private final RepositoryRemoteBinding unauthenticatedRepositoryRemoteBinding;
	private final RepositoryRemoteBinding requiringExplicitTicketRemoteBinding;
	private final RepositoryRemoteBinding authenticationRemoteBinding;

	private final String authenticationHeader;

	public AuthenticationServiceImpl(RepositoryRemoteBinding unauthenticatedRepositoryRemoteBinding,
			RepositoryRemoteBinding requiringExplicitTicketRemoteBinding,
			RepositoryRemoteBinding authenticationRemoteBinding, String authenticationHeader) {
		this.unauthenticatedRepositoryRemoteBinding = unauthenticatedRepositoryRemoteBinding;
		this.requiringExplicitTicketRemoteBinding = requiringExplicitTicketRemoteBinding;
		this.authenticationRemoteBinding = authenticationRemoteBinding;
		this.authenticationHeader = authenticationHeader;
	}

	@Override
	public RepositoryUser authenticate(String username) throws AccessDeniedRemoteException {
		return authenticationRemoteBinding.builder(AUTHENTICATED_USER_SERVICE_ENDPOINT)
				.header(authenticationHeader, username)
				.call();
	}

	@Override
	public RepositoryUser authenticate(RepositoryTicket ticket) throws AccessDeniedRemoteException {
		return requiringExplicitTicketRemoteBinding.builder(AUTHENTICATED_USER_SERVICE_ENDPOINT)
				.urlVariable(ticket)
				.call();
	}

	@Override
	public RepositoryUser authenticate(String username, String password) throws AccessDeniedRemoteException {
		LOGIN_REQUEST_SERVICE request = new LOGIN_REQUEST_SERVICE();
		request.username = username;
		request.password = password;
		
		return unauthenticatedRepositoryRemoteBinding.builder(LOGIN_REQUEST_SERVICE.ENDPOINT, request)
				.call();
	}

	@Override
	public RepositoryUser getAuthenticatedUser() {
		throw new UnsupportedOperationException("Use authenticate methods");
	}

	@Override
	public void logout(RepositoryTicket ticket) throws AccessDeniedRemoteException {
		requiringExplicitTicketRemoteBinding.builder(LOGOUT_SERVICE_ENDPOINT, ticket)
			.urlVariable(ticket)
			.call();
	}

}
