package fr.openwide.alfresco.repository.remote.authentication.web.script;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.authentication.model.RepositoryUser;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryUserRequest;
import fr.openwide.alfresco.repository.api.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.repository.api.remote.model.RepositoryRemoteException;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractPayloadRemoteWebScript;

public class LoginWebScript extends AbstractPayloadRemoteWebScript<RepositoryUser, RepositoryUserRequest> {

	private AuthenticationRemoteService authenticationRemoteService;

	@Override
	protected RepositoryUser executeImpl(RepositoryUserRequest request, Status status, Cache cache) throws RepositoryRemoteException {
		return authenticationRemoteService.authenticate(request);
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(RepositoryUserRequest.class);
	}

	public void setAuthenticationRemoteService(AuthenticationRemoteService authenticationRemoteService) {
		this.authenticationRemoteService = authenticationRemoteService;
	}

}
