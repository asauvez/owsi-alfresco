package fr.openwide.alfresco.repository.core.authority.web.script;

import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService.ADD_TO_GROUP;
import fr.openwide.alfresco.repository.core.node.web.script.AbstractNodeWebScript;

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
	protected JavaType getParameterType() {
		return SimpleType.construct(ADD_TO_GROUP.class);
	}

	public void setAuthorityRemoteService(AuthorityRemoteService authorityRemoteService) {
		this.authorityRemoteService = authorityRemoteService;
	}

}
