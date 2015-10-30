package fr.openwide.alfresco.repository.core.authority.web.script;

import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService.DELETE_GROUP;
import fr.openwide.alfresco.repository.core.node.web.script.AbstractNodeWebScript;

public class DeleteGroupWebScript extends AbstractNodeWebScript<Void, DELETE_GROUP> {

	protected AuthorityRemoteService authorityRemoteService;

	@Override
	protected Void execute(DELETE_GROUP payload) {
		authorityRemoteService.deleteGroup(
				Objects.requireNonNull(payload.groupShortName, "GroupShortName"));
		return null;
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(DELETE_GROUP.class);
	}

	public void setAuthorityRemoteService(AuthorityRemoteService authorityRemoteService) {
		this.authorityRemoteService = authorityRemoteService;
	}

}
