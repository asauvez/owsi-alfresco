package fr.openwide.alfresco.component.model.authority.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.openwide.alfresco.api.core.authentication.model.UserReference;
import fr.openwide.alfresco.api.core.authority.exception.AuthorityExistsRemoteException;
import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.authority.model.AuthorityTypeReference;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.authority.model.AuthorityQueryBuilder;
import fr.openwide.alfresco.component.model.authority.model.CachedGroup;
import fr.openwide.alfresco.component.model.authority.model.CachedUser;
import fr.openwide.alfresco.component.model.authority.service.AuthorityModelService;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.BusinessNodeList;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class AuthorityModelServiceImpl implements AuthorityModelService {

	private final AuthorityRemoteService authorityService;

	private final NodeScopeBuilder CACHED_USER_NODESCOPEBUILDER = new NodeScopeBuilder()
			.properties().set(CmModel.person.userName)
			.properties().set(CmModel.person.firstName)
			.properties().set(CmModel.person.lastName)
			.properties().set(CmModel.person.email);
	private final NodeScopeBuilder CACHED_GROUP_NODESCOPEBUILDER = new NodeScopeBuilder()
			.properties().set(CmModel.authorityContainer.authorityName)
			.properties().set(CmModel.authorityContainer.authorityDisplayName);
	
	private Map<String, CachedUser> cacheUsers = Collections.synchronizedMap(new LinkedHashMap<String, CachedUser>() {
		private static final int CACHE_SIZE = 100;
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, CachedUser> eldest) {
			return size() > CACHE_SIZE;
		}
	});
	private Map<AuthorityReference, CachedGroup> cacheGroups = Collections.synchronizedMap(new LinkedHashMap<AuthorityReference, CachedGroup>() {
		private static final int CACHE_SIZE = 50;
		@Override
		protected boolean removeEldestEntry(Map.Entry<AuthorityReference, CachedGroup> eldest) {
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
			BusinessNode node = getUser(userName, CACHED_USER_NODESCOPEBUILDER);
			cacheUsers.put(userName, nodeToCachedUser(node));
		}
		return user;
	}
	private CachedUser nodeToCachedUser(BusinessNode node) {
		return new CachedUser(new UserReference(node.properties().get(CmModel.person.userName)), 
				node.properties().get(CmModel.person.firstName),
				node.properties().get(CmModel.person.lastName),
				node.properties().get(CmModel.person.email));
	}
	@Override
	public void clearCachedUsers() {
		cacheUsers.clear();
	}
	
	
	@Override
	public List<BusinessNode> getContainedUsers(AuthorityQueryBuilder authorityQueryBuilder) {
		authorityQueryBuilder.type(AuthorityTypeReference.USER);
		
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
	public List<CachedUser> getContainedCachedUsers(AuthorityQueryBuilder authorityQueryBuilder) {
		authorityQueryBuilder.nodeScopeBuilder(CACHED_USER_NODESCOPEBUILDER);
		List<BusinessNode> nodes = getContainedUsers(authorityQueryBuilder);
		List<CachedUser> users = new ArrayList<>();
		for (BusinessNode node : nodes) {
			users.add(nodeToCachedUser(node));
		}
		return users;
	}
	
	@Override
	public List<BusinessNode> getContainedGroups(AuthorityQueryBuilder authorityQueryBuilder) {
		authorityQueryBuilder.type(AuthorityTypeReference.GROUP);
		
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
	public List<CachedGroup> getContainedCachedGroups(AuthorityQueryBuilder authorityQueryBuilder) {
		authorityQueryBuilder.nodeScopeBuilder(CACHED_GROUP_NODESCOPEBUILDER);
		List<BusinessNode> nodes = getContainedGroups(authorityQueryBuilder);
		List<CachedGroup> groups = new ArrayList<>();
		for (BusinessNode node : nodes) {
			groups.add(nodeToCachedGroup(node));
		}
		return groups;
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
	public Map<AuthorityReference, String> getContainedGroupsAsAuthority(AuthorityQueryBuilder authorityQueryBuilder) {
		authorityQueryBuilder.nodeScopeBuilder(new NodeScopeBuilder()
			.properties().set(CmModel.authorityContainer.authorityName)
			.properties().set(CmModel.authorityContainer.authorityDisplayName));
		List<BusinessNode> groups = getContainedGroups(authorityQueryBuilder);
		return getAsAuthority(groups);
	}

	private Map<AuthorityReference, String> getAsAuthority(List<BusinessNode> groups) {
		Map<AuthorityReference, String> authorities = new LinkedHashMap<>();
		for (BusinessNode node : groups) {
			String authorityName = node.properties().get(CmModel.authorityContainer.authorityName);
			String authorityDisplayName = node.properties().get(CmModel.authorityContainer.authorityDisplayName);
			authorities.put(AuthorityReference.authority(authorityName), authorityDisplayName);
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
	public void deleteUser(AuthorityReference user) throws NoSuchNodeRemoteException {
		authorityService.deleteUser(user.getName());
		cacheUsers.remove(user.getName());
	}

	@Override
	public void updateUserPassword(AuthorityReference user, String newPassword) throws NoSuchNodeRemoteException {
		authorityService.updateUserPassword(user.getName(), newPassword);
	}

	@Override
	public BusinessNode getGroup(AuthorityReference group, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeRemoteException {
		return new BusinessNode(authorityService.getGroup(group.getGroupShortName(), nodeScopeBuilder.getScope()));
	}
	@Override
	public CachedGroup getCachedGroup(AuthorityReference groupReference) throws NoSuchNodeRemoteException {
		CachedGroup group = cacheGroups.get(groupReference);
		if (group == null) {
			BusinessNode node = getGroup(groupReference, CACHED_GROUP_NODESCOPEBUILDER);
			cacheGroups.put(groupReference, nodeToCachedGroup(node));
		}
		return group;
	}
	private CachedGroup nodeToCachedGroup(BusinessNode node) {
		return new CachedGroup(
				AuthorityReference.authority(node.properties().get(CmModel.authorityContainer.authorityName)),
				node.properties().get(CmModel.authorityContainer.authorityDisplayName));
	}
	@Override
	public void clearCachedGroups() {
		cacheGroups.clear();
	}

	@Override
	public NodeReference createRootGroup(AuthorityReference group, String groupDisplayName) throws AuthorityExistsRemoteException {
		return createRootGroup(group, groupDisplayName, new NodeScopeBuilder().nodeReference()).getNodeReference();
	}
	@Override
	public BusinessNode createRootGroup(AuthorityReference group, String groupDisplayName,
			NodeScopeBuilder nodeScopeBuilder) throws AuthorityExistsRemoteException {
		return new BusinessNode(authorityService.createRootGroup(group.getGroupShortName(), groupDisplayName, nodeScopeBuilder.getScope()));
	}

	@Override
	public void deleteGroup(AuthorityReference group) throws NoSuchNodeRemoteException {
		authorityService.deleteGroup(group.getGroupShortName());
		cacheGroups.remove(group);
	}

	@Override
	public void addToGroup(AuthorityReference subAuthority, AuthorityReference parentGroup) throws NoSuchNodeRemoteException {
		authorityService.addToGroup(subAuthority.getName(), parentGroup.getGroupShortName());
	}

	@Override
	public void removeFromGroup(AuthorityReference subAuthority, AuthorityReference parentGroup) throws NoSuchNodeRemoteException {
		authorityService.removeFromGroup(subAuthority.getName(), parentGroup.getGroupShortName());
	}
}
