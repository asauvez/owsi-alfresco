package fr.openwide.alfresco.repo.core.authority.web.script;

import java.util.Objects;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService.REMOVE_FROM_GROUP;
import fr.openwide.alfresco.repo.core.node.web.script.AbstractNodeWebScript;

public class RemoveFromGroupWebScript extends AbstractNodeWebScript<Void, REMOVE_FROM_GROUP> {

	protected AuthorityRemoteService authorityRemoteService;

	@Override
	protected Void execute(REMOVE_FROM_GROUP payload) {
		authorityRemoteService.removeFromGroup(
				Objects.requireNonNull(payload.subAuthorityFullName, "SubAuthorityFullName"),
				Objects.requireNonNull(payload.parentGroupShortName, "ParentGroupShortName"));
		return null;
	}

	@Override
	protected Class<REMOVE_FROM_GROUP> getParameterType() {
		return REMOVE_FROM_GROUP.class;
	}

	public void setAuthorityRemoteService(AuthorityRemoteService authorityRemoteService) {
		this.authorityRemoteService = authorityRemoteService;
	}

}
