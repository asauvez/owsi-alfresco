package fr.openwide.alfresco.component.model.authority.service;

import java.util.List;
import java.util.Map;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.component.model.authority.model.AuthorityQueryBuilder;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;

public interface AuthorityModelService {

	BusinessNode getUser(String userName, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeRemoteException;
	
	List<BusinessNode> getContainedUsers(AuthorityQueryBuilder authorityQueryBuilder);

	List<BusinessNode> getContainedGroups(AuthorityQueryBuilder authorityQueryBuilder);
	Map<RepositoryAuthority, String> getContainedGroupsAsAuthority(AuthorityQueryBuilder authorityQueryBuilder);

}
