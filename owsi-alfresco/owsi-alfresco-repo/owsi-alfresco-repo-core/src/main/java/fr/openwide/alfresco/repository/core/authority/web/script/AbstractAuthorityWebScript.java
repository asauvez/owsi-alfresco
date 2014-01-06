package fr.openwide.alfresco.repository.core.authority.web.script;

import fr.openwide.alfresco.repository.api.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractMessageRemoteWebScript;

public abstract class AbstractAuthorityWebScript<R, P> extends AbstractMessageRemoteWebScript<R, P> {

	protected AuthorityRemoteService authorityRemoteService;

	public void setAuthorityRemoteService(AuthorityRemoteService authorityRemoteService) {
		this.authorityRemoteService = authorityRemoteService;
	}

}
