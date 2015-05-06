package fr.openwide.alfresco.repository.core.authority.web.script;

import fr.openwide.alfresco.repository.api.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.repository.core.node.web.script.AbstractNodeListWebScript;

public abstract class AbstractAuthorityWebScript<P> extends AbstractNodeListWebScript<P> {

	protected AuthorityRemoteService authorityRemoteService;

	public void setAuthorityRemoteService(AuthorityRemoteService authorityRemoteService) {
		this.authorityRemoteService = authorityRemoteService;
	}

}
