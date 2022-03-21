package fr.openwide.alfresco.app.core.authentication.service.impl;

import fr.openwide.alfresco.api.core.authentication.model.TicketReference;
import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.remote.exception.AccessDeniedRemoteException;
import fr.openwide.alfresco.app.core.authentication.service.AuthenticationService;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;

public class AuthenticationServiceImpl implements AuthenticationService {

	private final RepositoryRemoteBinding unauthenticatedRepositoryRemoteBinding;
	private final RepositoryRemoteBinding requiringExplicitTicketRemoteBinding;
	private final RepositoryRemoteBinding authenticationRemoteBinding;

	private final String authenticationHeader;
	private final NodeScope defaultUserNodeScope;
	
	public AuthenticationServiceImpl(
			RepositoryRemoteBinding unauthenticatedRepositoryRemoteBinding,
			RepositoryRemoteBinding requiringExplicitTicketRemoteBinding,
			RepositoryRemoteBinding authenticationRemoteBinding, 
			String authenticationHeader) {
		this.unauthenticatedRepositoryRemoteBinding = unauthenticatedRepositoryRemoteBinding;
		this.requiringExplicitTicketRemoteBinding = requiringExplicitTicketRemoteBinding;
		this.authenticationRemoteBinding = authenticationRemoteBinding;
		this.authenticationHeader = authenticationHeader;

		defaultUserNodeScope = new NodeScopeBuilder()
				.properties().set(CmModel.person.firstName)
				.properties().set(CmModel.person.lastName)
				.properties().set(CmModel.person.email)
				.getScope();
	}

	@Override
	public RepositoryUser authenticate(String username) throws AccessDeniedRemoteException {
		return authenticate(username, getDefaultUserNodeScope());
	}
	
	@Override
	public RepositoryUser authenticate(String username, NodeScope nodeScope) throws AccessDeniedRemoteException {
		AUTHENTICATED_USER_SERVICE request = new AUTHENTICATED_USER_SERVICE();
		request.nodeScope = nodeScope;
		
		return authenticationRemoteBinding.builder(request)
				.header(authenticationHeader, username)
				.call();
	}

	@Override
	public RepositoryUser authenticate(TicketReference ticket) throws AccessDeniedRemoteException {
		return authenticate(ticket, getDefaultUserNodeScope());
	}

	@Override
	public RepositoryUser authenticate(TicketReference ticket, NodeScope nodeScope) throws AccessDeniedRemoteException {
		AUTHENTICATED_USER_SERVICE request = new AUTHENTICATED_USER_SERVICE();
		request.nodeScope = nodeScope;

		return requiringExplicitTicketRemoteBinding.builder(request)
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
		
		return unauthenticatedRepositoryRemoteBinding.builder(request)
				.call();
	}

	@Override
	public RepositoryUser getAuthenticatedUser(NodeScope nodeScope) {
		throw new UnsupportedOperationException("Use authenticate methods");
	}
	@Override
	public String getAuthenticatedUsername() {
		throw new UnsupportedOperationException("Use authenticate methods");
	}

	@Override
	public void logout(TicketReference ticket) throws AccessDeniedRemoteException {
		requiringExplicitTicketRemoteBinding.builder(new LOGOUT_SERVICE())
			.urlVariable(ticket)
			.call();
	}

	@Override
	public NodeScope getDefaultUserNodeScope() {
		return defaultUserNodeScope;
	}
}
