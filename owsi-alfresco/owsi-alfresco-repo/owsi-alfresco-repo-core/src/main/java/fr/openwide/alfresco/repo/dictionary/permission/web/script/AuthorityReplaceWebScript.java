package fr.openwide.alfresco.repo.dictionary.permission.web.script;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.WebScriptRequest;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.repo.dictionary.permission.service.PermissionRepositoryService;
import fr.openwide.alfresco.repo.remote.framework.web.script.AbstractMessageRemoteWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;

@GenerateWebScript(
		url={
			"/owsi/authorityReplace?old={old}&new={new}",
			"/owsi/authorityReplace?old={old}&new={new}&maxItem={maxItem}"
		},
		shortName="Replace une authority par une autre",
		transactionAllow=GenerateWebScriptTransactionAllow.READWRITE,
		family="OWSI",
		beanParent="webscript.owsi.remote")
public class AuthorityReplaceWebScript extends AbstractMessageRemoteWebScript<Integer, WebScriptRequest> {

	@Autowired
	private PermissionRepositoryService permissionRepositoryService;
	
	@Override
	protected WebScriptRequest extractPayload(WebScriptRequest req) {
		return req;
	}
	
	@Override
	protected Integer execute(WebScriptRequest req) {
		AuthorityReference oldAuthority = AuthorityReference.authority(req.getParameter("old"));
		AuthorityReference newAuthority = AuthorityReference.authority(req.getParameter("new"));
		String maxItem = req.getParameter("maxItem");
		
		return permissionRepositoryService.replaceAuthority(oldAuthority, newAuthority, 
				Optional.ofNullable(maxItem).map(Integer::parseInt));
	}
	
}
