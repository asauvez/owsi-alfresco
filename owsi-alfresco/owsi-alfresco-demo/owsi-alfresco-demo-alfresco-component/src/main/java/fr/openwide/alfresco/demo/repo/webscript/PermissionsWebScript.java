package fr.openwide.alfresco.demo.repo.webscript;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.WebScriptRequest;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.node.model.RepositoryAccessControl;
import fr.openwide.alfresco.repo.dictionary.permission.service.PermissionRepositoryService;
import fr.openwide.alfresco.repo.remote.framework.web.script.AbstractMessageRemoteWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;

@GenerateWebScript(
		url="/demo/permissions?authority={authority}",
		shortName="List node permissions for an authority",
		transactionAllow=GenerateWebScriptTransactionAllow.READONLY,
		family="Demo",
		beanParent="webscript.owsi.remote")
public class PermissionsWebScript extends AbstractMessageRemoteWebScript<List<RepositoryAccessControl>, AuthorityReference> {

	@Autowired
	private PermissionRepositoryService permissionRepositoryService;
	
	@Override
	protected AuthorityReference extractPayload(WebScriptRequest req) {
		return AuthorityReference.authority(req.getParameter("authority"));
	}
	
	@Override
	protected List<RepositoryAccessControl> execute(AuthorityReference authority) {
		List<RepositoryAccessControl> list = permissionRepositoryService.searchACL(authority);
		return list;
	}
	
}
