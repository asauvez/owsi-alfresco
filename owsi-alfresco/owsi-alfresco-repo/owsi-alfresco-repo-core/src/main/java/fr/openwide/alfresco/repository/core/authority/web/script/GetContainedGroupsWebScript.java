package fr.openwide.alfresco.repository.core.authority.web.script;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.authority.service.AuthorityRemoteService.GET_CONTAINED_GROUPS;
import fr.openwide.alfresco.repository.api.node.model.RemoteCallParameters;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;

public class GetContainedGroupsWebScript extends AbstractAuthorityWebScript<GET_CONTAINED_GROUPS> {

	@Override
	protected List<RepositoryNode> execute(GET_CONTAINED_GROUPS payload) {
		return authorityRemoteService.getContainedGroups(
				Objects.requireNonNull(payload.authority, "Authority"), 
				payload.immediate, 
				Objects.requireNonNull(payload.nodeScope, "NodeScope"), 
				payload.remoteCallParameters);
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(GET_CONTAINED_GROUPS.class);
	}

	@Override
	protected RemoteCallParameters getRemoteCallParameters(GET_CONTAINED_GROUPS payload) {
		return payload.remoteCallParameters;
	}
}
