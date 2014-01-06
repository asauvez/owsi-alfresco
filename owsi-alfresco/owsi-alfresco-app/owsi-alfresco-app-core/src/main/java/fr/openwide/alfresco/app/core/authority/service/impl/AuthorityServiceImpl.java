package fr.openwide.alfresco.app.core.authority.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;

import fr.openwide.alfresco.app.core.authority.service.AuthorityService;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryAuthority;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;

@Service
public class AuthorityServiceImpl implements AuthorityService {

	@Autowired
	private RepositoryRemoteBinding repositoryRemoteBinding;

	@Override
	public List<RepositoryNode> getContainedUsers(RepositoryAuthority authority, boolean immediate, NodeFetchDetails nodeFetchDetails) {
		try {
			GET_CONTAINED_USERS request = new GET_CONTAINED_USERS();
			request.authority = authority;
			request.immediate = immediate;
			request.nodeFetchDetails = nodeFetchDetails;
			return repositoryRemoteBinding.exchangeCollection(GET_CONTAINED_USERS.URL, 
					GET_CONTAINED_USERS.METHOD, request, new ParameterizedTypeReference<List<RepositoryNode>>() {});
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}
	
	@Override
	public List<RepositoryNode> getContainedGroups(RepositoryAuthority authority, boolean immediate, NodeFetchDetails nodeFetchDetails) {
		try {
			GET_CONTAINED_GROUPS request = new GET_CONTAINED_GROUPS();
			request.authority = authority;
			request.immediate = immediate;
			request.nodeFetchDetails = nodeFetchDetails;
			return repositoryRemoteBinding.exchangeCollection(GET_CONTAINED_GROUPS.URL, 
					GET_CONTAINED_GROUPS.METHOD, request, new ParameterizedTypeReference<List<RepositoryNode>>() {});
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

}
