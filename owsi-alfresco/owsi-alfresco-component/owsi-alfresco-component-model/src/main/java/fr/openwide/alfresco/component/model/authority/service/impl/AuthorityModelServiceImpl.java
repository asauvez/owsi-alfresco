package fr.openwide.alfresco.component.model.authority.service.impl;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.component.model.authority.model.AuthorityQueryBuilder;
import fr.openwide.alfresco.component.model.authority.service.AuthorityModelService;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.BusinessNodeList;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class AuthorityModelServiceImpl implements AuthorityModelService {

	private final AuthorityRemoteService authorityService;

	public AuthorityModelServiceImpl(AuthorityRemoteService authorityService) {
		this.authorityService = authorityService;
	}

	@Override
	public BusinessNode getUser(String userName, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeRemoteException {
		return new BusinessNode(authorityService.getUser(userName, nodeScopeBuilder.getScope()));
	}
	
	@Override
	public List<BusinessNode> getContainedUsers(AuthorityQueryBuilder authorityQueryBuilder) {
		if (authorityQueryBuilder.getSearchParameters().getFilterProperty() == null) {
			authorityQueryBuilder.filterProperty(CmModel.person.lastName);
		}
		if (authorityQueryBuilder.getSearchParameters().getNodeScope() == null) {
			authorityQueryBuilder.nodeScopeBuilder(new NodeScopeBuilder()
				.nodeReference()
				.properties().set(CmModel.person));
		}
		return new BusinessNodeList(authorityService.getContainedUsers(authorityQueryBuilder.getSearchParameters()));
	}
	
	@Override
	public List<BusinessNode> getContainedGroups(AuthorityQueryBuilder authorityQueryBuilder) {
		if (authorityQueryBuilder.getSearchParameters().getFilterProperty() == null) {
			authorityQueryBuilder.filterProperty(CmModel.authorityContainer.authorityDisplayName);
		}
		if (authorityQueryBuilder.getSearchParameters().getNodeScope() == null) {
			authorityQueryBuilder.nodeScopeBuilder(new NodeScopeBuilder()
				.nodeReference()
				.properties().set(CmModel.authorityContainer));
		}
		return new BusinessNodeList(authorityService.getContainedGroups(authorityQueryBuilder.getSearchParameters()));
	}

	@Override
	public Map<RepositoryAuthority, String> getContainedGroupsAsAuthority(AuthorityQueryBuilder authorityQueryBuilder) {
		authorityQueryBuilder.nodeScopeBuilder(new NodeScopeBuilder()
			.properties().set(CmModel.authorityContainer.authorityName)
			.properties().set(CmModel.authorityContainer.authorityDisplayName));
		List<BusinessNode> groups = getContainedGroups(authorityQueryBuilder);
		return getAsAuthority(groups);
	}

	private Map<RepositoryAuthority, String> getAsAuthority(List<BusinessNode> groups) {
		Map<RepositoryAuthority, String> authorities = new LinkedHashMap<>();
		for (BusinessNode node : groups) {
			String authorityName = node.properties().get(CmModel.authorityContainer.authorityName);
			String authorityDisplayName = node.properties().get(CmModel.authorityContainer.authorityDisplayName);
			authorities.put(new RepositoryAuthority(authorityName), authorityDisplayName);
		}
		return authorities;
	}
}
