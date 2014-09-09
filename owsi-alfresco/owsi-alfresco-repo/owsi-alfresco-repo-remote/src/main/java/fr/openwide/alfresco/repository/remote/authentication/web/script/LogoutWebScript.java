package fr.openwide.alfresco.repository.remote.authentication.web.script;

import java.util.Objects;

import org.springframework.extensions.webscripts.WebScriptRequest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.repository.api.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractParameterRemoteWebScript;

public class LogoutWebScript extends AbstractParameterRemoteWebScript<Void, RepositoryTicket> {

	private AuthenticationRemoteService authenticationRemoteService;

	@Override
	protected Void executeImpl(RepositoryTicket ticket, WebScriptRequest req) {
		authenticationRemoteService.logout(
				Objects.requireNonNull(ticket, "RepositoryTicket"));
		return null;
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(RepositoryTicket.class);
	}

	public void setAuthenticationRemoteService(AuthenticationRemoteService authenticationRemoteService) {
		this.authenticationRemoteService = authenticationRemoteService;
	}

}
