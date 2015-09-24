package fr.openwide.alfresco.repository.remote.authentication.web.script;

import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService.LOGIN_REQUEST_SERVICE;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractMessageRemoteWebScript;

public class LoginWebScript extends AbstractMessageRemoteWebScript<RepositoryUser, LOGIN_REQUEST_SERVICE> {

	private AuthenticationRemoteService authenticationRemoteService;

	@Override
	protected RepositoryUser executeImpl(LOGIN_REQUEST_SERVICE request) {
		return authenticationRemoteService.authenticate(
				Objects.requireNonNull(request.username, "Username"), 
				Objects.requireNonNull(request.password, "Password"),
				Objects.requireNonNull(request.nodeScope, "NodeScope"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(LOGIN_REQUEST_SERVICE.class);
	}

	public void setAuthenticationRemoteService(AuthenticationRemoteService authenticationRemoteService) {
		this.authenticationRemoteService = authenticationRemoteService;
	}

}
