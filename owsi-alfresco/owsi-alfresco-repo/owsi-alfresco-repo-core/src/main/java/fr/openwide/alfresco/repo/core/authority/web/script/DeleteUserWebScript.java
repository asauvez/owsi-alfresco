package fr.openwide.alfresco.repo.core.authority.web.script;

import java.util.Objects;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService.DELETE_USER;
import fr.openwide.alfresco.repo.core.node.web.script.AbstractNodeWebScript;

public class DeleteUserWebScript extends AbstractNodeWebScript<Void, DELETE_USER> {

	protected AuthorityRemoteService authorityRemoteService;

	@Override
	protected Void execute(DELETE_USER payload) {
		authorityRemoteService.deleteUser(
				Objects.requireNonNull(payload.userName, "UserName"));
		return null;
	}

	@Override
	protected Class<DELETE_USER> getParameterType() {
		return DELETE_USER.class;
	}

	public void setAuthorityRemoteService(AuthorityRemoteService authorityRemoteService) {
		this.authorityRemoteService = authorityRemoteService;
	}

}
