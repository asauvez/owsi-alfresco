package fr.openwide.alfresco.component.model.authority.service;

import java.util.List;
import java.util.Map;

import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeFetchDetailsBuilder;
import fr.openwide.alfresco.repository.api.node.model.RepositoryAuthority;

/**
 * @author asauvez
 */
public interface AuthorityModelService {

	List<BusinessNode> getContainedUsers(RepositoryAuthority authority, boolean immediate);
	List<BusinessNode> getContainedUsers(RepositoryAuthority authority, boolean immediate, NodeFetchDetailsBuilder nodeFetchDetailsBuilder);

	List<BusinessNode> getContainedGroups(RepositoryAuthority authority, boolean immediate);
	Map<RepositoryAuthority, String> getContainedGroupsAsAuthority(RepositoryAuthority authority, boolean immediate);
	List<BusinessNode> getContainedGroups(RepositoryAuthority authority, boolean immediate, NodeFetchDetailsBuilder nodeFetchDetailsBuilder);

}
