package fr.openwide.alfresco.repository.remote.authentication.web.script;

import java.io.IOException;

import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractRemoteWebScript;

public class UsernameWebScript extends AbstractRemoteWebScript<String, Void> {

	private AuthenticationRemoteService authenticationRemoteService;

	@Override
	protected Void extractPayload(WebScriptRequest req) {
		return null;
	}
	@Override
	protected String executeImpl(Void payload) {
		return authenticationRemoteService.getAuthenticatedUsername();
	}
	@Override
	protected void handleResult(WebScriptResponse res, String resValue) throws IOException {
		res.setContentType("text/plain;charset=UTF-8");
		res.getWriter().write(resValue);
	}

	public void setAuthenticationRemoteService(AuthenticationRemoteService authenticationRemoteService) {
		this.authenticationRemoteService = authenticationRemoteService;
	}

}
