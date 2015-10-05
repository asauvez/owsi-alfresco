package fr.openwide.alfresco.repo.module.bootstrap.service;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;


public interface BootstrapService {

	RepositoryAuthority createGroup(RepositoryAuthority group, RepositoryAuthority ... parentGroups);
	RepositoryAuthority createGroup(String groupName, RepositoryAuthority ... parentGroups);

	RepositoryAuthority createUser(String username, String firstName, String lastName, String email, String password, RepositoryAuthority ... parentGroups);
	RepositoryAuthority createTestUser(String username, RepositoryAuthority ... parentGroups);

	NodeReference createRootCategory(String categoryName);
	NodeReference createCategory(NodeReference parentCategory, String categoryName);

	NodeReference importFileFromClassPath(NodeReference parentRef, String fileName);
	void importView(NodeReference parentRef, String fileName);

}
