package fr.openwide.alfresco.repository.core.authority.web.script;

import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService.CREATE_ROOT_GROUP;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.core.node.web.script.AbstractNodeWebScript;

public class CreateRootGroupWebScript extends AbstractNodeWebScript<RepositoryNode, CREATE_ROOT_GROUP> {

	protected AuthorityRemoteService authorityRemoteService;

	@Override
	protected RepositoryNode execute(CREATE_ROOT_GROUP payload) {
		return authorityRemoteService.createRootGroup(
				Objects.requireNonNull(payload.groupShortName, "GroupShortName"),
				Objects.requireNonNull(payload.groupDisplayName, "GroupDisplayName"),
				Objects.requireNonNull(payload.nodeScope, "NodeScope"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(CREATE_ROOT_GROUP.class);
	}

	public void setAuthorityRemoteService(AuthorityRemoteService authorityRemoteService) {
		this.authorityRemoteService = authorityRemoteService;
	}

}
