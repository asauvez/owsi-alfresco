package fr.openwide.alfresco.component.model.authority.service.impl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.openwide.alfresco.api.core.authentication.model.UserReference;
import fr.openwide.alfresco.api.core.authority.exception.AuthorityExistsRemoteException;
import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthorityType;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.authority.model.AuthorityQueryBuilder;
import fr.openwide.alfresco.component.model.authority.model.CachedUser;
import fr.openwide.alfresco.component.model.authority.service.AuthorityModelService;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.BusinessNodeList;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class AuthorityModelServiceImpl implements AuthorityModelService {

	private final AuthorityRemoteService authorityService;

	private Map<String, CachedUser> cacheUsers = Collections.synchronizedMap(new LinkedHashMap<String, CachedUser>() {
		private static final int CACHE_SIZE = 100;
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, CachedUser> eldest) {
			return size() > CACHE_SIZE;
		}
	});
	
	public AuthorityModelServiceImpl(AuthorityRemoteService authorityService) {
		this.authorityService = authorityService;
	}

	@Override
	public BusinessNode getUser(String userName, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeRemoteException {
		return new BusinessNode(authorityService.getUser(userName, nodeScopeBuilder.getScope()));
	}
	
	@Override
	public CachedUser getCachedUser(String userName) throws NoSuchNodeRemoteException {
		CachedUser user = cacheUsers.get(userName);
		if (user == null) {
			BusinessNode node = getUser(userName, new NodeScopeBuilder()
				.properties().set(CmModel.person.firstName)
				.properties().set(CmModel.person.lastName)
				.properties().set(CmModel.person.email));
			user = new CachedUser(new UserReference(userName), 
				node.properties().get(CmModel.person.firstName),
				node.properties().get(CmModel.person.lastName),
				node.properties().get(CmModel.person.email));
			cacheUsers.put(userName, user);
		}
		return user;
	}
	@Override
	public void clearCachedUser() {
		cacheUsers.clear();
	}
	
	
	@Override
	public List<BusinessNode> getContainedUsers(AuthorityQueryBuilder authorityQueryBuilder) {
		authorityQueryBuilder.type(RepositoryAuthorityType.USER);
		
		if (authorityQueryBuilder.getParameters().getFilterProperty() == null) {
			authorityQueryBuilder.filterProperty(CmModel.person.lastName);
		}
		if (authorityQueryBuilder.getParameters().getNodeScope() == null) {
			authorityQueryBuilder.nodeScopeBuilder(new NodeScopeBuilder()
				.nodeReference()
				.properties().set(CmModel.person));
		}
		return getContainedAuthorities(authorityQueryBuilder);
	}
	
	@Override
	public List<BusinessNode> getContainedGroups(AuthorityQueryBuilder authorityQueryBuilder) {
		authorityQueryBuilder.type(RepositoryAuthorityType.GROUP);
		
		if (authorityQueryBuilder.getParameters().getFilterProperty() == null) {
			authorityQueryBuilder.filterProperty(CmModel.authorityContainer.authorityDisplayName);
		}
		if (authorityQueryBuilder.getParameters().getNodeScope() == null) {
			authorityQueryBuilder.nodeScopeBuilder(new NodeScopeBuilder()
				.nodeReference()
				.properties().set(CmModel.authorityContainer));
		}
		return getContainedAuthorities(authorityQueryBuilder);
	}
	
	@Override
	public List<BusinessNode> getContainedAuthorities(AuthorityQueryBuilder authorityQueryBuilder) {
		if (authorityQueryBuilder.getParameters().getNodeScope() == null) {
			authorityQueryBuilder.nodeScopeBuilder(new NodeScopeBuilder()
				.nodeReference()
				.properties().set(CmModel.person)
				.properties().set(CmModel.authorityContainer));
		}
		return new BusinessNodeList(authorityService.getContainedAuthorities(authorityQueryBuilder.getParameters()));
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
			authorities.put(RepositoryAuthority.authority(authorityName), authorityDisplayName);
		}
		return authorities;
	}

	@Override
	public NodeReference createUser(String userName, String firstName, String lastName, String email, String password) throws AuthorityExistsRemoteException {
		return createUser(userName, firstName, lastName, email, password, new NodeScopeBuilder().nodeReference()).getNodeReference();
	}
	@Override
	public BusinessNode createUser(String userName, String firstName, String lastName, String email, String password,
			NodeScopeBuilder nodeScopeBuilder) throws AuthorityExistsRemoteException {
		return new BusinessNode(authorityService.createUser(userName, firstName, lastName, email, password, nodeScopeBuilder.getScope()));
	}

	@Override
	public void deleteUser(RepositoryAuthority user) throws NoSuchNodeRemoteException {
		authorityService.deleteUser(user.getName());
	}

	@Override
	public void updateUserPassword(RepositoryAuthority user, String newPassword) throws NoSuchNodeRemoteException {
		authorityService.updateUserPassword(user.getName(), newPassword);
	}

	@Override
	public BusinessNode getGroup(RepositoryAuthority group, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeRemoteException {
		return new BusinessNode(authorityService.getGroup(group.getGroupShortName(), nodeScopeBuilder.getScope()));
	}

	@Override
	public NodeReference createRootGroup(RepositoryAuthority group, String groupDisplayName) throws AuthorityExistsRemoteException {
		return createRootGroup(group, groupDisplayName, new NodeScopeBuilder().nodeReference()).getNodeReference();
	}
	@Override
	public BusinessNode createRootGroup(RepositoryAuthority group, String groupDisplayName,
			NodeScopeBuilder nodeScopeBuilder) throws AuthorityExistsRemoteException {
		return new BusinessNode(authorityService.createRootGroup(group.getGroupShortName(), groupDisplayName, nodeScopeBuilder.getScope()));
	}

	@Override
	public void deleteGroup(RepositoryAuthority group) throws NoSuchNodeRemoteException {
		authorityService.deleteGroup(group.getGroupShortName());
	}

	@Override
	public void addToGroup(RepositoryAuthority subAuthority, RepositoryAuthority parentGroup) throws NoSuchNodeRemoteException {
		authorityService.addToGroup(subAuthority.getName(), parentGroup.getGroupShortName());
	}

	@Override
	public void removeFromGroup(RepositoryAuthority subAuthority, RepositoryAuthority parentGroup) throws NoSuchNodeRemoteException {
		authorityService.removeFromGroup(subAuthority.getName(), parentGroup.getGroupShortName());
	}
}
