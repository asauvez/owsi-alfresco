package fr.openwide.alfresco.component.model.authority.service;

import java.util.List;
import java.util.Map;

import fr.openwide.alfresco.api.core.authority.exception.AuthorityExistsRemoteException;
import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.authority.model.AuthorityQueryBuilder;
import fr.openwide.alfresco.component.model.authority.model.CachedGroup;
import fr.openwide.alfresco.component.model.authority.model.CachedUser;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;

public interface AuthorityModelService {

	BusinessNode getUser(String userName, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeRemoteException;
	CachedUser getCachedUser(String userName) throws NoSuchNodeRemoteException;
	void clearCachedUsers();

	BusinessNode getGroup(AuthorityReference group, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeRemoteException;
	CachedGroup getCachedGroup(AuthorityReference groupReference) throws NoSuchNodeRemoteException;
	void clearCachedGroups();

	List<BusinessNode> getContainedAuthorities(AuthorityQueryBuilder authorityQueryBuilder);

	List<BusinessNode> getContainedUsers(AuthorityQueryBuilder authorityQueryBuilder);
	List<CachedUser> getContainedCachedUsers(AuthorityQueryBuilder authorityQueryBuilder);

	List<BusinessNode> getContainedGroups(AuthorityQueryBuilder authorityQueryBuilder);
	Map<AuthorityReference, String> getContainedGroupsAsAuthority(AuthorityQueryBuilder authorityQueryBuilder);
	List<CachedGroup> getContainedCachedGroups(AuthorityQueryBuilder authorityQueryBuilder);


	NodeReference createUser(String userName, String firstName, String lastName, String email, String password) throws AuthorityExistsRemoteException;
	BusinessNode createUser(String userName, String firstName, String lastName, String email, String password, NodeScopeBuilder nodeScopeBuilder) throws AuthorityExistsRemoteException;
	
	NodeReference createRootGroup(AuthorityReference group, String groupDisplayName) throws AuthorityExistsRemoteException;
	BusinessNode createRootGroup(AuthorityReference group, String groupDisplayName, NodeScopeBuilder nodeScopeBuilder) throws AuthorityExistsRemoteException;

	void updateUserPassword(AuthorityReference user, String newPassword) throws NoSuchNodeRemoteException;

	void deleteUser(AuthorityReference user) throws NoSuchNodeRemoteException;
	void deleteGroup(AuthorityReference group) throws NoSuchNodeRemoteException;
	
	void addToGroup(AuthorityReference subAuthority, AuthorityReference parentGroup) throws NoSuchNodeRemoteException;
	void removeFromGroup(AuthorityReference subAuthority, AuthorityReference parentGroup) throws NoSuchNodeRemoteException;
}
