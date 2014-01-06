package fr.openwide.alfresco.component.model.authority.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.app.core.authority.service.AuthorityService;
import fr.openwide.alfresco.component.model.authority.service.AuthorityModelService;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeFetchDetailsBuilder;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.node.model.RepositoryAuthority;

public class AuthorityModelServiceImpl implements AuthorityModelService {

	@Autowired
	private AuthorityService authorityService;

	@Override
	public List<BusinessNode> getContainedUsers(RepositoryAuthority authority, boolean immediate) {
		return getContainedUsers(authority, immediate, new NodeFetchDetailsBuilder()
				.nodeReference()
				.properties(CmModel.person));
	}

	@Override
	public List<BusinessNode> getContainedUsers(RepositoryAuthority authority, boolean immediate, NodeFetchDetailsBuilder nodeFetchDetailsBuilder) {
		return BusinessNode.wrapList(authorityService.getContainedUsers(authority, immediate, nodeFetchDetailsBuilder.getDetails()));
	}

	@Override
	public List<BusinessNode> getContainedGroups(RepositoryAuthority authority, boolean immediate) {
		return getContainedGroups(authority, immediate, new NodeFetchDetailsBuilder()
				.nodeReference()
				.properties(CmModel.authorityContainer));
	}

	@Override
	public Map<RepositoryAuthority, String> getContainedGroupsAsAuthority(RepositoryAuthority authority, boolean immediate) {
		List<BusinessNode> groups = getContainedGroups(authority, immediate);
		Map<RepositoryAuthority, String> authorities = new LinkedHashMap<RepositoryAuthority, String>();
		for (BusinessNode node : groups) {
			String authorityName = node.getProperty(CmModel.authorityContainer.authorityName);
			String authorityDisplayName = node.getProperty(CmModel.authorityContainer.authorityDisplayName);
			authorities.put(new RepositoryAuthority(authorityName), authorityDisplayName);
		}
		return authorities;
	}

	@Override
	public List<BusinessNode> getContainedGroups(RepositoryAuthority authority, boolean immediate, NodeFetchDetailsBuilder nodeFetchDetailsBuilder) {
		return BusinessNode.wrapList(authorityService.getContainedGroups(authority, immediate, nodeFetchDetailsBuilder.getDetails()));
	}

}
