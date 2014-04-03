package fr.openwide.alfresco.repository.core.authority.web.script;

import java.util.List;
import java.util.Objects;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.authority.service.AuthorityRemoteService.GET_CONTAINED_GROUPS;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;

public class GetContainedGroupsWebScript extends AbstractAuthorityWebScript<List<RepositoryNode>, GET_CONTAINED_GROUPS> {

	@Override
	protected List<RepositoryNode> executeImpl(GET_CONTAINED_GROUPS payload, Status status, Cache cache) {
		return authorityRemoteService.getContainedGroups(
				Objects.requireNonNull(payload.authority, "Authority"), 
				payload.immediate, 
				Objects.requireNonNull(payload.nodeScope, "NodeScope"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(GET_CONTAINED_GROUPS.class);
	}

}
