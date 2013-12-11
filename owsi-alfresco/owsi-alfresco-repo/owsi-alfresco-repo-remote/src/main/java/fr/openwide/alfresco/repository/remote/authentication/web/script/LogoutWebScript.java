package fr.openwide.alfresco.repository.remote.authentication.web.script;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.repository.api.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.repository.api.remote.model.RepositoryRemoteException;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractParameterRemoteWebScript;

public class LogoutWebScript extends AbstractParameterRemoteWebScript<Void, RepositoryTicket> {

	private AuthenticationRemoteService authenticationRemoteService;

	@Override
	protected Void executeImpl(RepositoryTicket ticket, Status status, Cache cache) throws RepositoryRemoteException {
		authenticationRemoteService.logout(ticket);
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
