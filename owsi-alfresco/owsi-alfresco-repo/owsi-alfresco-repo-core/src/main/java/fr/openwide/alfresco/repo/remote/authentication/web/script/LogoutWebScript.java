package fr.openwide.alfresco.repo.remote.authentication.web.script;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.WebScriptRequest;

import fr.openwide.alfresco.api.core.authentication.model.TicketReference;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService.LOGOUT_SERVICE;
import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.remote.framework.web.script.AbstractMessageRemoteWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;

@GenerateWebScript(
		paramClass=LOGOUT_SERVICE.class,
		shortName="logout",
		description="Logout a previously authenticated user using its ticket",
		formatDefault="json",
		transactionAllow=GenerateWebScriptTransactionAllow.READONLY,
		family=OwsiSwaggerWebScript.WS_FAMILY,
		beanParent="webscript.owsi.remote")
public class LogoutWebScript extends AbstractMessageRemoteWebScript<Void, TicketReference> {

	@Autowired
	private AuthenticationRemoteService authenticationRemoteService;

	@Override
	protected Void execute(TicketReference ticket) {
		authenticationRemoteService.logout(Objects.requireNonNull(ticket, "TicketReference"));
		return null;
	}

	@Override
	protected TicketReference extractPayload(WebScriptRequest req) {
		return new TicketReference(req.getParameter("alf_ticket"));
	}
	
}
