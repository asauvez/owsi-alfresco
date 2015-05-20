package fr.openwide.alfresco.app.core.authority.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.app.core.authority.service.AuthorityService;
import fr.openwide.alfresco.app.core.node.service.NodeService;

@Service
public class AuthorityServiceImpl implements AuthorityService {

	@Autowired
	private NodeService nodeService;

	@Override
	public List<RepositoryNode> getContainedUsers(RepositoryAuthority authority, boolean immediate, NodeScope nodeScope) {
		GET_CONTAINED_USERS payload = new GET_CONTAINED_USERS();
		payload.authority = authority;
		payload.immediate = immediate;
		payload.nodeScope = nodeScope;
		return nodeService.callNodeListSerializer(GET_CONTAINED_USERS.ENDPOINT, payload, nodeScope);
	}
	
	@Override
	public List<RepositoryNode> getContainedGroups(RepositoryAuthority authority, boolean immediate, NodeScope nodeScope) {
		GET_CONTAINED_GROUPS payload = new GET_CONTAINED_GROUPS();
		payload.authority = authority;
		payload.immediate = immediate;
		payload.nodeScope = nodeScope;
		return nodeService.callNodeListSerializer(GET_CONTAINED_GROUPS.ENDPOINT, payload, nodeScope);
	}

}
