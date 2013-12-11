package fr.openwide.alfresco.app.core.authentication.service.impl;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import fr.openwide.alfresco.app.core.authentication.service.AuthenticationService;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryPayloadParameterHandler;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryUser;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryUserRequest;
import fr.openwide.alfresco.repository.api.authentication.model.UserReference;
import fr.openwide.alfresco.repository.api.remote.model.AccessDeniedRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.InvalidPayloadRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.RepositoryRemoteException;
import fr.openwide.core.jpa.exception.SecurityServiceException;

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

	@Autowired
	private RepositoryPayloadParameterHandler payloadParameterHandler;

	private String authenticationHeader;

	@PostConstruct
	private void initFromEnvironment() {
		authenticationHeader = environment.getRequiredProperty("application.authentication.repository.header.name");
	}

	@Override
	public RepositoryUser authenticate(String username) throws SecurityServiceException {
		UserReference userReference = new UserReference(username);
		HttpHeaders headers = new HttpHeaders();
		headers.add(authenticationHeader, userReference.getUsername());
		try {
			return authenticationRemoteBinding.exchange(AUTHENTICATED_USER_SERVICE.URL, 
					AUTHENTICATED_USER_SERVICE.METHOD, userReference, RepositoryUser.class, headers);
		} catch (AccessDeniedRemoteException e) {
			throw new SecurityServiceException(e);
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

	@Override
	public RepositoryUser authenticate(RepositoryTicket ticket) throws SecurityServiceException {
		try {
			return requiringExplicitTicketRemoteBinding.exchange(AUTHENTICATED_USER_SERVICE.URL, 
					AUTHENTICATED_USER_SERVICE.METHOD, RepositoryUser.class, ticket);
		} catch (AccessDeniedRemoteException e) {
			throw new SecurityServiceException(e);
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

	@Override
	public RepositoryUser authenticate(String username, String password) throws SecurityServiceException, RepositoryRemoteException {
		RepositoryUserRequest request = new RepositoryUserRequest();
		request.setUsername(username);
		request.setPassword(password);
		try {
			return authenticate(request);
		} catch (AccessDeniedRemoteException e) {
			throw new SecurityServiceException(e);
		}
	}

	@Override
	public RepositoryUser authenticate(RepositoryUserRequest request) throws RepositoryRemoteException {
		return unauthenticatedRepositoryRemoteBinding.exchange(LOGIN_REQUEST_SERVICE.URL, 
				LOGIN_REQUEST_SERVICE.METHOD, request, RepositoryUser.class);
	}

	@Override
	public RepositoryUser getAuthenticatedUser() {
		throw new UnsupportedOperationException("Use authenticate methods");
	}

	@Override
	public void logout(RepositoryTicket ticket) throws AccessDeniedRemoteException {
		try {
			HttpHeaders headers = payloadParameterHandler.handlePayload(ticket);
			requiringExplicitTicketRemoteBinding.exchange(LOGOUT_SERVICE.URL, LOGOUT_SERVICE.METHOD, ticket, Void.class, headers, ticket);
		} catch (AccessDeniedRemoteException e) {
			throw e;
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

}
