package fr.openwide.alfresco.repository.core.authority.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RemoteCallParameters;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService;
import fr.openwide.alfresco.repository.core.remote.service.ConversionService;

public class AuthorityRemoteServiceImpl implements AuthorityRemoteService {

	private NodeRemoteService nodeRemoteService;
	private ConversionService conversionService;

	private AuthorityService authorityService;

	@Override
	public List<RepositoryNode> getContainedUsers(RepositoryAuthority repoAuthority, boolean immediate, NodeScope nodeScope, RemoteCallParameters remoteCallParameters) {
		return getContained(repoAuthority, AuthorityType.USER, immediate, nodeScope, remoteCallParameters);
	}

	@Override
	public List<RepositoryNode> getContainedGroups(RepositoryAuthority repoAuthority, boolean immediate, NodeScope nodeScope, RemoteCallParameters remoteCallParameters) {
		return getContained(repoAuthority, AuthorityType.GROUP, immediate, nodeScope, remoteCallParameters);
	}

	private List<RepositoryNode> getContained(RepositoryAuthority repoAuthority, AuthorityType type, boolean immediate, NodeScope nodeScope, RemoteCallParameters remoteCallParameters) {
		Set<String> authorities = authorityService.getContainedAuthorities(type, repoAuthority.getName(), immediate);
		List<RepositoryNode> nodes = new ArrayList<RepositoryNode>();
		for (String authority : authorities) {
			NodeRef nodeRef = authorityService.getAuthorityNodeRef(authority);
			nodes.add(nodeRemoteService.get(conversionService.get(nodeRef), nodeScope, remoteCallParameters));
		}
		return nodes;
	}
	
	public void setNodeRemoteService(NodeRemoteService nodeRemoteService) {
		this.nodeRemoteService = nodeRemoteService;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}
}
