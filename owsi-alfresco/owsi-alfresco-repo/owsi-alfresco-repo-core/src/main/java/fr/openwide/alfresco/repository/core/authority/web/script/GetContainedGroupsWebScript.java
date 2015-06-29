package fr.openwide.alfresco.repository.core.authority.web.script;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService.GET_CONTAINED_GROUPS;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;

public class GetContainedGroupsWebScript extends AbstractAuthorityWebScript<GET_CONTAINED_GROUPS> {

	@Override
	protected List<RepositoryNode> execute(GET_CONTAINED_GROUPS payload) {
		return authorityRemoteService.getContainedGroups(
				Objects.requireNonNull(payload.searchParameters, "SearchParameters"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(GET_CONTAINED_GROUPS.class);
	}

}
