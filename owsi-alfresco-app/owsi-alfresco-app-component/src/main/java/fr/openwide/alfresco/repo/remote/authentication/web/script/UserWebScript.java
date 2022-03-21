package fr.openwide.alfresco.repo.remote.authentication.web.script;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService.AUTHENTICATED_USER_SERVICE;
import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.remote.framework.web.script.AbstractMessageRemoteWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptFormatDefault;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;

@GenerateWebScript(
		paramClass=AUTHENTICATED_USER_SERVICE.class,
		shortName="user",
		description="Retrieve user information using a reference",
		formatDefaultEnum=GenerateWebScriptFormatDefault.JSON,
		transactionAllow=GenerateWebScriptTransactionAllow.READONLY,
		family=OwsiSwaggerWebScript.WS_FAMILY,
		visibleInSwagger=false,
		beanParent="webscript.owsi.remote")
public class UserWebScript extends AbstractMessageRemoteWebScript<RepositoryUser, AUTHENTICATED_USER_SERVICE> {

	@Autowired
	private AuthenticationRemoteService authenticationRemoteService;

	@Override
	protected RepositoryUser execute(AUTHENTICATED_USER_SERVICE request) { 
		return authenticationRemoteService.getAuthenticatedUser(Objects.requireNonNull(request.nodeScope, "NodeScope"));
	}
	
	@Override 
	protected Class<AUTHENTICATED_USER_SERVICE> getParameterType() { 
	 	return AUTHENTICATED_USER_SERVICE.class; 
	}

}
