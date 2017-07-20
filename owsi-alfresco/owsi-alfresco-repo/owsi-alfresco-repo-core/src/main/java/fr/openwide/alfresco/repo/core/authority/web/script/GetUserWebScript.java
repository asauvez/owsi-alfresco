package fr.openwide.alfresco.repo.core.authority.web.script;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService.GET_USER;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.repo.core.node.web.script.AbstractNodeWebScript;

public class GetUserWebScript extends AbstractNodeWebScript<RepositoryNode, GET_USER> {

	protected AuthorityRemoteService authorityRemoteService;

	@Override
	protected RepositoryNode execute(GET_USER payload) {
		return authorityRemoteService.getUser(
				Objects.requireNonNull(payload.userName, "UserName"), 
				Objects.requireNonNull(payload.nodeScope, "NodeScope"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(GET_USER.class);
	}

	@Override
	protected Collection<RepositoryNode> getOutputNodes(RepositoryNode result) {
		return Collections.singleton(result);
	}

	public void setAuthorityRemoteService(AuthorityRemoteService authorityRemoteService) {
		this.authorityRemoteService = authorityRemoteService;
	}
	
}
