package fr.openwide.alfresco.repository.remote.authentication.web.script;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.WebScriptRequest;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService.LOGOUT_SERVICE;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractMessageRemoteWebScript;
import fr.openwide.alfresco.repository.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repository.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;

@GenerateWebScript(
		paramClass=LOGOUT_SERVICE.class,
		shortName="logout",
		description="Logout a previously authenticated user using its ticket",
		formatDefault="json",
		transactionAllow=GenerateWebScriptTransactionAllow.READONLY,
		family="OWSI",
		beanParent="webscript.owsi.remote")
public class LogoutWebScript extends AbstractMessageRemoteWebScript<Void, RepositoryTicket> {

	@Autowired
	private AuthenticationRemoteService authenticationRemoteService;

	@Override
	protected Void execute(RepositoryTicket ticket) {
		authenticationRemoteService.logout(Objects.requireNonNull(ticket, "RepositoryTicket"));
		return null;
	}

	@Override
	protected RepositoryTicket extractPayload(WebScriptRequest req) {
		return new RepositoryTicket(req.getParameter("alf_ticket"));
	}
	
}
