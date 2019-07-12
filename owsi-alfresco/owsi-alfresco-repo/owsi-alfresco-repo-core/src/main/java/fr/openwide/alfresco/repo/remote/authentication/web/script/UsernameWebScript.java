package fr.openwide.alfresco.repo.remote.authentication.web.script;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService.AUTHENTICATED_USERNAME_SERVICE;
import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.remote.framework.web.script.AbstractRemoteWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptFormatDefault;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;

@GenerateWebScript(
		paramClass=AUTHENTICATED_USERNAME_SERVICE.class,
		shortName="username",
		description="Retrieve current username",
		formatDefaultEnum=GenerateWebScriptFormatDefault.HTML,
		transactionAllow=GenerateWebScriptTransactionAllow.READONLY,
		family=OwsiSwaggerWebScript.WS_FAMILY,
		beanParent="webscript.owsi.remote")
public class UsernameWebScript extends AbstractRemoteWebScript<String, Void> {

	@Autowired
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

}
