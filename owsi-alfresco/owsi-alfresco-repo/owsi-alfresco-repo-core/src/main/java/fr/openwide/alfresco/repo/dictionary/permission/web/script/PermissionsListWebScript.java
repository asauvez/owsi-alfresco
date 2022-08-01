package fr.openwide.alfresco.repo.dictionary.permission.web.script;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.node.model.RepositoryAccessControl;
import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.dictionary.permission.service.PermissionRepositoryService;
import fr.openwide.alfresco.repo.remote.framework.web.script.AbstractMessageRemoteWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;
import fr.openwide.alfresco.repo.wsgenerator.annotation.SwaggerParameter;

/**
 * http://localhost:8080/alfresco/s/owsi/permissions.csv?authority=admin
 * http://localhost:8080/alfresco/s/owsi/permissions.csv?authority=GROUP_EVERYONE
 */
@GenerateWebScript(
		url="/owsi/permissions.csv",
		shortName="List node permissions for an authority",
		authentication = GenerateWebScriptAuthentication.ADMIN,
		transactionAllow=GenerateWebScriptTransactionAllow.READONLY,
		family=OwsiSwaggerWebScript.WS_FAMILY,
		swaggerParameters={
			@SwaggerParameter(name="authority", required = true),
			@SwaggerParameter(name="includeParent", description="Doit on renvoyer les permissions attribuées à des groupes parents."),
		})
public class PermissionsListWebScript extends AbstractMessageRemoteWebScript<String, WebScriptRequest> {

	@Autowired
	private PermissionRepositoryService permissionRepositoryService;
	@Autowired
	private NodeModelRepositoryService nodeModelRepositoryService;
	
	@Override
	protected WebScriptRequest extractPayload(WebScriptRequest req) {
		return req;
	}
	@Override
	protected void handleResult(WebScriptResponse res, String resValue) throws IOException {
		res.setContentType("text/csv");
		res.getWriter().append(resValue);
	}
	
	@Override
	protected String execute(WebScriptRequest req) {
		AuthorityReference authority = AuthorityReference.authority(req.getParameter("authority"));
		boolean includeParent = Boolean.parseBoolean(getParameter(req, "includeParent", "false"));
		
		List<RepositoryAccessControl> list = (includeParent)
				? permissionRepositoryService.searchACLwithParentAuthorities(authority)
				: permissionRepositoryService.searchACL(authority);
		
		StringBuilder buf = new StringBuilder();
		for (RepositoryAccessControl acl : list) {
			buf
				.append(acl.getNodeRef()).append(";")
				.append(acl.getAuthority()).append(";")
				.append(acl.getPermission()).append(";")
				.append(acl.isAllowed()).append(";")
				.append(nodeModelRepositoryService.getPath(acl.getNodeRef())).append(";")
				.append("\n");
		}
		return buf.toString();
	}
	
}
