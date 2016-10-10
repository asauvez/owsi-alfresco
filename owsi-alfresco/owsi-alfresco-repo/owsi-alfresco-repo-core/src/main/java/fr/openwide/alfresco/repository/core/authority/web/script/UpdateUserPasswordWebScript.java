package fr.openwide.alfresco.repository.core.authority.web.script;

import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService.UPDATE_USER_PASSWORD;
import fr.openwide.alfresco.repository.core.node.web.script.AbstractNodeWebScript;

public class UpdateUserPasswordWebScript extends AbstractNodeWebScript<Void, UPDATE_USER_PASSWORD> {

	protected AuthorityRemoteService authorityRemoteService;

	@Override
	protected Void execute(UPDATE_USER_PASSWORD payload) {
		authorityRemoteService.updateUserPassword(
				Objects.requireNonNull(payload.userName, "UserName"),
				Objects.requireNonNull(payload.newPassword, "NewPassword"));
		return null;
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(UPDATE_USER_PASSWORD.class);
	}

	public void setAuthorityRemoteService(AuthorityRemoteService authorityRemoteService) {
		this.authorityRemoteService = authorityRemoteService;
	}

}
