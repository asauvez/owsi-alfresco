package fr.openwide.alfresco.component.model.authority.service;

import java.util.List;
import java.util.Map;

import fr.openwide.alfresco.api.core.authority.exception.AuthorityExistsRemoteException;
import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.authority.model.AuthorityQueryBuilder;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;

public interface AuthorityModelService {

	BusinessNode getUser(String userName, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeRemoteException;
	BusinessNode getGroup(RepositoryAuthority group, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeRemoteException;
	
	List<BusinessNode> getContainedUsers(AuthorityQueryBuilder authorityQueryBuilder);

	List<BusinessNode> getContainedGroups(AuthorityQueryBuilder authorityQueryBuilder);
	Map<RepositoryAuthority, String> getContainedGroupsAsAuthority(AuthorityQueryBuilder authorityQueryBuilder);

	List<BusinessNode> getContainedAuthorities(AuthorityQueryBuilder authorityQueryBuilder);

	NodeReference createUser(String userName, String firstName, String lastName, String email, String password) throws AuthorityExistsRemoteException;
	BusinessNode createUser(String userName, String firstName, String lastName, String email, String password, NodeScopeBuilder nodeScopeBuilder) throws AuthorityExistsRemoteException;
	
	NodeReference createRootGroup(RepositoryAuthority group, String groupDisplayName) throws AuthorityExistsRemoteException;
	BusinessNode createRootGroup(RepositoryAuthority group, String groupDisplayName, NodeScopeBuilder nodeScopeBuilder) throws AuthorityExistsRemoteException;

	void updateUserPassword(RepositoryAuthority user, String newPassword) throws NoSuchNodeRemoteException;

	void deleteUser(RepositoryAuthority user) throws NoSuchNodeRemoteException;
	void deleteGroup(RepositoryAuthority group) throws NoSuchNodeRemoteException;
	
	void addToGroup(RepositoryAuthority subAuthority, RepositoryAuthority parentGroup) throws NoSuchNodeRemoteException;
	void removeFromGroup(RepositoryAuthority subAuthority, RepositoryAuthority parentGroup) throws NoSuchNodeRemoteException;
}
