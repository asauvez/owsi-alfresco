package fr.openwide.alfresco.app.core.authentication.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import fr.openwide.alfresco.app.core.authentication.service.AuthenticationService;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryUser;
import fr.openwide.alfresco.repository.api.remote.exception.AccessDeniedRemoteException;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	@Autowired
	@Qualifier("unauthenticatedRepositoryRemoteBinding")
	private RepositoryRemoteBinding unauthenticatedRepositoryRemoteBinding;

	@Autowired
	@Qualifier("requiringExplicitTicketRemoteBinding")
	private RepositoryRemoteBinding requiringExplicitTicketRemoteBinding;

	@Autowired
	@Qualifier("authenticationRemoteBinding")
	private RepositoryRemoteBinding authenticationRemoteBinding;

	@Autowired
	private Environment environment;

	private String authenticationHeader;

	@PostConstruct
	private void initFromEnvironment() {
		authenticationHeader = environment.getRequiredProperty("application.authentication.repository.header.name");
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
		requiringExplicitTicketRemoteBinding.builder(LOGOUT_SERVICE_ENDPOINT)
			.headerPayload(ticket)
			.urlVariable(ticket)
			.call();
	}

}
