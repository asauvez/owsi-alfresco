package fr.openwide.alfresco.repo.module.bootstrap.service;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;


public interface BootstrapService {

	AuthorityReference createGroup(AuthorityReference group, AuthorityReference ... parentAuthorities);
	AuthorityReference createGroup(String groupName, AuthorityReference ... parentAuthorities);
	void importGroupsFile(String fileName);

	AuthorityReference createUser(String username, String firstName, String lastName, String email, String password, AuthorityReference ... parentAuthorities);
	AuthorityReference createTestUser(String username, AuthorityReference ... parentAuthorities);

	NodeReference createRootCategory(String categoryName);
	NodeReference createCategory(NodeReference parentCategory, String categoryName);

	NodeReference importFileFromClassPath(NodeReference parentRef, String fileName);
	void importView(NodeReference parentRef, String viewFileName, String messageFileName);

}
