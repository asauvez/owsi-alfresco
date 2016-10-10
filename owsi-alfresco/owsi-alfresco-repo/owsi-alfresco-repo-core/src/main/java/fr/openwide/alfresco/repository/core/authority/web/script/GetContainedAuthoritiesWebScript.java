package fr.openwide.alfresco.repository.core.authority.web.script;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService.GET_CONTAINED_AUTHORITIES;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;

public class GetContainedAuthoritiesWebScript extends AbstractAuthorityWebScript<GET_CONTAINED_AUTHORITIES> {

	@Override
	protected List<RepositoryNode> execute(GET_CONTAINED_AUTHORITIES payload) {
		return authorityRemoteService.getContainedAuthorities(
				Objects.requireNonNull(payload.searchParameters, "SearchParameters"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(GET_CONTAINED_AUTHORITIES.class);
	}

}
