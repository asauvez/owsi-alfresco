package fr.openwide.alfresco.repository.core.authority.web.script;

import fr.openwide.alfresco.repository.api.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.repository.core.node.web.script.AbstractNodeWebScript;

public abstract class AbstractAuthorityWebScript<R, P> extends AbstractNodeWebScript<R, P> {

	protected AuthorityRemoteService authorityRemoteService;

	public void setAuthorityRemoteService(AuthorityRemoteService authorityRemoteService) {
		this.authorityRemoteService = authorityRemoteService;
	}

}
