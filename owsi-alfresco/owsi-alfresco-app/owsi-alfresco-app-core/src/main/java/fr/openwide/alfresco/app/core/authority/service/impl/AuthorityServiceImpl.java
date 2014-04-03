package fr.openwide.alfresco.app.core.authority.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.alfresco.app.core.authority.service.AuthorityService;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryAuthority;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;

@Service
public class AuthorityServiceImpl implements AuthorityService {

	@Autowired
	private RepositoryRemoteBinding repositoryRemoteBinding;

	@Override
	public List<RepositoryNode> getContainedUsers(RepositoryAuthority authority, boolean immediate, NodeScope nodeScope) {
		GET_CONTAINED_USERS request = new GET_CONTAINED_USERS();
		request.authority = authority;
		request.immediate = immediate;
		request.nodeScope = nodeScope;
		
		return repositoryRemoteBinding.builder(GET_CONTAINED_USERS.ENDPOINT)
				.headerPayload(request)
				.call();
	}
	
	@Override
	public List<RepositoryNode> getContainedGroups(RepositoryAuthority authority, boolean immediate, NodeScope nodeScope) {
		GET_CONTAINED_GROUPS request = new GET_CONTAINED_GROUPS();
		request.authority = authority;
		request.immediate = immediate;
		request.nodeScope = nodeScope;
		
		return repositoryRemoteBinding.builder(GET_CONTAINED_GROUPS.ENDPOINT)
				.headerPayload(request)
				.call();
	}

}
