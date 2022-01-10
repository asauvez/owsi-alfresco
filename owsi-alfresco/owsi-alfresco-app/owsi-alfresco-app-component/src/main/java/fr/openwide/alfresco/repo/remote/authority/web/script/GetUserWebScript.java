package fr.openwide.alfresco.repo.remote.authority.web.script;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService.GET_USER;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.repo.remote.node.web.script.AbstractNodeWebScript;

public class GetUserWebScript extends AbstractNodeWebScript<RepositoryNode, GET_USER> {

	protected AuthorityRemoteService authorityRemoteService;

	@Override
	protected RepositoryNode execute(GET_USER payload) {
		return authorityRemoteService.getUser(
				Objects.requireNonNull(payload.userName, "UserName"), 
				Objects.requireNonNull(payload.nodeScope, "NodeScope"));
	}

	@Override
	protected Class<GET_USER> getParameterType() {
		return GET_USER.class;
	}

	@Override
	protected Collection<RepositoryNode> getOutputNodes(RepositoryNode result) {
		return Collections.singleton(result);
	}

	public void setAuthorityRemoteService(AuthorityRemoteService authorityRemoteService) {
		this.authorityRemoteService = authorityRemoteService;
	}
	
}
