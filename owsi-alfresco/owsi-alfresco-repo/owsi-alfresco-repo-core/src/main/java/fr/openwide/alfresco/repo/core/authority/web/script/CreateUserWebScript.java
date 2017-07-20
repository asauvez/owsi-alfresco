package fr.openwide.alfresco.repo.core.authority.web.script;

import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService.CREATE_USER;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.repo.core.node.web.script.AbstractNodeWebScript;

public class CreateUserWebScript extends AbstractNodeWebScript<RepositoryNode, CREATE_USER> {

	protected AuthorityRemoteService authorityRemoteService;

	@Override
	protected RepositoryNode execute(CREATE_USER payload) {
		return authorityRemoteService.createUser(
				Objects.requireNonNull(payload.userName, "UserName"),
				Objects.requireNonNull(payload.firstName, "FirstName"),
				Objects.requireNonNull(payload.lastName, "LastName"),
				Objects.requireNonNull(payload.email, "Email"),
				Objects.requireNonNull(payload.password, "Password"),
				Objects.requireNonNull(payload.nodeScope, "NodeScope"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(CREATE_USER.class);
	}

	public void setAuthorityRemoteService(AuthorityRemoteService authorityRemoteService) {
		this.authorityRemoteService = authorityRemoteService;
	}

}
