package fr.openwide.alfresco.app.core.authority.service.impl;

import java.util.List;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthorityQueryParameters;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.app.core.authority.service.AuthorityService;
import fr.openwide.alfresco.app.core.node.service.NodeService;

public class AuthorityServiceImpl implements AuthorityService {

	private final NodeService nodeService;

	public AuthorityServiceImpl(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	public RepositoryNode getUser(String userName, NodeScope nodeScope) throws NoSuchNodeRemoteException {
		GET_USER payload = new GET_USER();
		payload.userName = userName;
		payload.nodeScope = nodeScope;
		return nodeService.callNodeSerializer(GET_USER.ENDPOINT, payload, nodeScope);
	}
	
	@Override
	public List<RepositoryNode> getContainedUsers(RepositoryAuthorityQueryParameters searchParameters) {
		GET_CONTAINED_USERS payload = new GET_CONTAINED_USERS();
		payload.searchParameters = searchParameters;
		return nodeService.callNodeListSerializer(GET_CONTAINED_USERS.ENDPOINT, payload, searchParameters.getNodeScope());
	}
	
	@Override
	public List<RepositoryNode> getContainedGroups(RepositoryAuthorityQueryParameters searchParameters) {
		GET_CONTAINED_GROUPS payload = new GET_CONTAINED_GROUPS();
		payload.searchParameters = searchParameters;
		return nodeService.callNodeListSerializer(GET_CONTAINED_GROUPS.ENDPOINT, payload, searchParameters.getNodeScope());
	}

}
