package fr.openwide.alfresco.repo.remote.authority.web.script;

import java.util.Objects;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService.DELETE_GROUP;
import fr.openwide.alfresco.repo.remote.node.web.script.AbstractNodeWebScript;

public class DeleteGroupWebScript extends AbstractNodeWebScript<Void, DELETE_GROUP> {

	protected AuthorityRemoteService authorityRemoteService;

	@Override
	protected Void execute(DELETE_GROUP payload) {
		authorityRemoteService.deleteGroup(
				Objects.requireNonNull(payload.groupShortName, "GroupShortName"));
		return null;
	}

	@Override
	protected Class<DELETE_GROUP> getParameterType() {
		return DELETE_GROUP.class;
	}

	public void setAuthorityRemoteService(AuthorityRemoteService authorityRemoteService) {
		this.authorityRemoteService = authorityRemoteService;
	}

}
