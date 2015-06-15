package fr.openwide.alfresco.repository.remote.authentication.web.script;

import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService.AUTHENTICATED_USER_SERVICE;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractMessageRemoteWebScript;

public class UserWebScript extends AbstractMessageRemoteWebScript<RepositoryUser, AUTHENTICATED_USER_SERVICE> {

	private AuthenticationRemoteService authenticationRemoteService;

	@Override
	protected RepositoryUser executeImpl(AUTHENTICATED_USER_SERVICE request) {
		return authenticationRemoteService.getAuthenticatedUser(
				Objects.requireNonNull(request.nodeScope, "NodeScope"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(AUTHENTICATED_USER_SERVICE.class);
	}

	public void setAuthenticationRemoteService(AuthenticationRemoteService authenticationRemoteService) {
		this.authenticationRemoteService = authenticationRemoteService;
	}

}
