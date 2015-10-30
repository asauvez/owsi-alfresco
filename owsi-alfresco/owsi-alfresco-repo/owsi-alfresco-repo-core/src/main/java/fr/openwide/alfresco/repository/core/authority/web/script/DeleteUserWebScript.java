package fr.openwide.alfresco.repository.core.authority.web.script;

import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService.DELETE_USER;
import fr.openwide.alfresco.repository.core.node.web.script.AbstractNodeWebScript;

public class DeleteUserWebScript extends AbstractNodeWebScript<Void, DELETE_USER> {

	protected AuthorityRemoteService authorityRemoteService;

	@Override
	protected Void execute(DELETE_USER payload) {
		authorityRemoteService.deleteUser(
				Objects.requireNonNull(payload.userName, "UserName"));
		return null;
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(DELETE_USER.class);
	}

	public void setAuthorityRemoteService(AuthorityRemoteService authorityRemoteService) {
		this.authorityRemoteService = authorityRemoteService;
	}

}
