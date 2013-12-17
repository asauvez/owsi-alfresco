package fr.openwide.alfresco.repository.remote.authentication.web.script;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.authentication.model.RepositoryUser;
import fr.openwide.alfresco.repository.api.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.repository.api.authentication.service.AuthenticationRemoteService.LOGIN_REQUEST_SERVICE;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractPayloadRemoteWebScript;

public class LoginWebScript extends AbstractPayloadRemoteWebScript<RepositoryUser, LOGIN_REQUEST_SERVICE> {

	private AuthenticationRemoteService authenticationRemoteService;

	@Override
	protected RepositoryUser executeImpl(LOGIN_REQUEST_SERVICE request, Status status, Cache cache) throws RepositoryRemoteException {
		return authenticationRemoteService.authenticate(request.username, request.password);
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(LOGIN_REQUEST_SERVICE.class);
	}

	public void setAuthenticationRemoteService(AuthenticationRemoteService authenticationRemoteService) {
		this.authenticationRemoteService = authenticationRemoteService;
	}

}
