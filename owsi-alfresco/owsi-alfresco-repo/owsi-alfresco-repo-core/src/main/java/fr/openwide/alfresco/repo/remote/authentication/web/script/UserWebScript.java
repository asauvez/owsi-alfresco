package fr.openwide.alfresco.repo.remote.authentication.web.script;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService.AUTHENTICATED_USER_SERVICE;
import fr.openwide.alfresco.repo.remote.framework.web.script.AbstractMessageRemoteWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;

@GenerateWebScript(
		paramClass=AUTHENTICATED_USER_SERVICE.class,
		shortName="user",
		description="Retrieve user information using a reference",
		formatDefault="json",
		transactionAllow=GenerateWebScriptTransactionAllow.READONLY,
		family="OWSI",
		beanParent="webscript.owsi.remote")
public class UserWebScript extends AbstractMessageRemoteWebScript<RepositoryUser, AUTHENTICATED_USER_SERVICE> {

	@Autowired
	private AuthenticationRemoteService authenticationRemoteService;

	@Override
	protected RepositoryUser execute(AUTHENTICATED_USER_SERVICE request) { 
		return authenticationRemoteService.getAuthenticatedUser(Objects.requireNonNull(request.nodeScope, "NodeScope"));
	}
	
	@Override 
	protected JavaType getParameterType() { 
	 	return SimpleType.construct(AUTHENTICATED_USER_SERVICE.class); 
	}

}
