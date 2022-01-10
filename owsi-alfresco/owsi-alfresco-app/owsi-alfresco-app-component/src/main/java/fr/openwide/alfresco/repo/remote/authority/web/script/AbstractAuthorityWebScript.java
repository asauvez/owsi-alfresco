package fr.openwide.alfresco.repo.remote.authority.web.script;

import java.util.List;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.repo.remote.node.web.script.AbstractNodeListWebScript;
import fr.openwide.alfresco.repo.wsgenerator.model.WebScriptParam;

public abstract class AbstractAuthorityWebScript<P extends WebScriptParam<List<RepositoryNode>>> 
		extends AbstractNodeListWebScript<P> {

	protected AuthorityRemoteService authorityRemoteService;

	public void setAuthorityRemoteService(AuthorityRemoteService authorityRemoteService) {
		this.authorityRemoteService = authorityRemoteService;
	}

}
