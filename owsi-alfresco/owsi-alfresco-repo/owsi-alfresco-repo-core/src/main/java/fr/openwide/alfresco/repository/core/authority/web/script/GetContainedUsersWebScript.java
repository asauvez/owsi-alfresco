package fr.openwide.alfresco.repository.core.authority.web.script;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService.GET_CONTAINED_USERS;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;

public class GetContainedUsersWebScript extends AbstractAuthorityWebScript<GET_CONTAINED_USERS> {

	@Override
	protected List<RepositoryNode> execute(GET_CONTAINED_USERS payload) {
		return authorityRemoteService.getContainedUsers(
				Objects.requireNonNull(payload.authority, "Authority"), 
				payload.immediate,
				Objects.requireNonNull(payload.nodeScope, "NodeScope"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(GET_CONTAINED_USERS.class);
	}

}
