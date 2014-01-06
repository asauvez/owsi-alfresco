package fr.openwide.alfresco.repository.core.authority.web.script;

import java.util.List;
import java.util.Objects;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.authority.service.AuthorityRemoteService.GET_CONTAINED_USERS;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;

public class GetContainedUsersWebScript extends AbstractAuthorityWebScript<List<RepositoryNode>, GET_CONTAINED_USERS> {

	@Override
	protected List<RepositoryNode> executeImpl(GET_CONTAINED_USERS payload, Status status, Cache cache) throws RepositoryRemoteException {
		return authorityRemoteService.getContainedUsers(
				Objects.requireNonNull(payload.authority, "Authority"), 
				payload.immediate, 
				Objects.requireNonNull(payload.nodeFetchDetails, "NodeFetchDetails"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(GET_CONTAINED_USERS.class);
	}

}
