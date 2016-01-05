package fr.openwide.alfresco.repository.remote.authentication.web.script;

import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractMessageRemoteWebScript;

public class LogoutWebScript extends AbstractMessageRemoteWebScript<Void, RepositoryTicket> {

	private AuthenticationRemoteService authenticationRemoteService;

	@Override
	protected Void execute(RepositoryTicket ticket) {
		authenticationRemoteService.logout(Objects.requireNonNull(ticket, "RepositoryTicket"));
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
