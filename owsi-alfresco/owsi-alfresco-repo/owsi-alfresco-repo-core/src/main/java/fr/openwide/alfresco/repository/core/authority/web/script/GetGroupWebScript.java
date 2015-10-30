package fr.openwide.alfresco.repository.core.authority.web.script;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService.GET_GROUP;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.core.node.web.script.AbstractNodeWebScript;

public class GetGroupWebScript extends AbstractNodeWebScript<RepositoryNode, GET_GROUP> {

	protected AuthorityRemoteService authorityRemoteService;

	@Override
	protected RepositoryNode execute(GET_GROUP payload) {
		return authorityRemoteService.getGroup(
				Objects.requireNonNull(payload.groupShortName, "GroupName"), 
				Objects.requireNonNull(payload.nodeScope, "NodeScope"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(GET_GROUP.class);
	}

	@Override
	protected Collection<RepositoryNode> getOutputNodes(RepositoryNode result) {
		return Collections.singleton(result);
	}

	public void setAuthorityRemoteService(AuthorityRemoteService authorityRemoteService) {
		this.authorityRemoteService = authorityRemoteService;
	}
	
}
