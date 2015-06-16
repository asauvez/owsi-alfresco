package fr.openwide.alfresco.repository.remote.authentication.web.script;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractRemoteWebScript;

public class UserWebScript extends AbstractRemoteWebScript<RepositoryUser> {

	private AuthenticationRemoteService authenticationRemoteService;

	@Override
	protected RepositoryUser executeImpl(WebScriptRequest req, WebScriptResponse res, Status status, Cache cache) { 
		return authenticationRemoteService.getAuthenticatedUser(); 
	}

	public void setAuthenticationRemoteService(AuthenticationRemoteService authenticationRemoteService) {
		this.authenticationRemoteService = authenticationRemoteService;
	}

}
