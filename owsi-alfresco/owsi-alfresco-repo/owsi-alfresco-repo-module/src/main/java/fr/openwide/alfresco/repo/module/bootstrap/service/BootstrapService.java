package fr.openwide.alfresco.repo.module.bootstrap.service;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;


public interface BootstrapService {

	RepositoryAuthority createGroup(RepositoryAuthority group, RepositoryAuthority ... parentAuthorities);
	RepositoryAuthority createGroup(String groupName, RepositoryAuthority ... parentAuthorities);
	void importGroupsFile(String fileName);

	RepositoryAuthority createUser(String username, String firstName, String lastName, String email, String password, RepositoryAuthority ... parentAuthorities);
	RepositoryAuthority createTestUser(String username, RepositoryAuthority ... parentAuthorities);

	NodeReference createRootCategory(String categoryName);
	NodeReference createCategory(NodeReference parentCategory, String categoryName);

	NodeReference importFileFromClassPath(NodeReference parentRef, String fileName);
	void importView(NodeReference parentRef, String viewFileName, String messageFileName);

}
