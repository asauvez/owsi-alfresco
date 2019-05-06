package fr.openwide.alfresco.repo.remote.authentication.web.script;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService.LOGIN_REQUEST_SERVICE;
import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.remote.framework.web.script.AbstractMessageRemoteWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;

@GenerateWebScript(
		paramClass=LOGIN_REQUEST_SERVICE.class,
		shortName="login",
		description="Login using username and password",
		formatDefault="json",
		authentication=GenerateWebScriptAuthentication.NONE,
		transactionAllow=GenerateWebScriptTransactionAllow.READONLY,
		family=OwsiSwaggerWebScript.WS_FAMILY,
		beanParent="webscript.owsi.remote")
public class LoginWebScript extends AbstractMessageRemoteWebScript<RepositoryUser, LOGIN_REQUEST_SERVICE> {

	@Autowired
	private AuthenticationRemoteService authenticationRemoteService;

	@Override
	protected RepositoryUser execute(LOGIN_REQUEST_SERVICE request) {
		return authenticationRemoteService.authenticate(
				Objects.requireNonNull(request.username, "Username"), 
				Objects.requireNonNull(request.password, "Password"),
				Objects.requireNonNull(request.nodeScope, "NodeScope"));
	}

	@Override
	protected Class<LOGIN_REQUEST_SERVICE> getParameterType() {
		return LOGIN_REQUEST_SERVICE.class;
	}

}
