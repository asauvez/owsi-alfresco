package fr.openwide.alfresco.repo.remote.authority.web.script;

import java.util.Objects;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService.ADD_TO_GROUP;
import fr.openwide.alfresco.repo.remote.node.web.script.AbstractNodeWebScript;

public class AddToGroupWebScript extends AbstractNodeWebScript<Void, ADD_TO_GROUP> {

	protected AuthorityRemoteService authorityRemoteService;

	@Override
	protected Void execute(ADD_TO_GROUP payload) {
		authorityRemoteService.addToGroup(
				Objects.requireNonNull(payload.subAuthorityFullName, "SubAuthorityFullName"),
				Objects.requireNonNull(payload.parentGroupShortName, "ParentGroupShortName"));
		return null;
	}

	@Override
	protected Class<ADD_TO_GROUP> getParameterType() {
		return ADD_TO_GROUP.class;
	}

	public void setAuthorityRemoteService(AuthorityRemoteService authorityRemoteService) {
		this.authorityRemoteService = authorityRemoteService;
	}

}
