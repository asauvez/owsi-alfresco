package fr.openwide.alfresco.repo.core.bootstrap.service;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteVisibility;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;


public interface BootstrapService {

	AuthorityReference createGroup(AuthorityReference group, AuthorityReference ... parentAuthorities);
	AuthorityReference createGroup(String groupName, AuthorityReference ... parentAuthorities);
	void importGroupsFile(String fileName);

	AuthorityReference createUser(String username, String firstName, String lastName, String email, String password, AuthorityReference ... parentAuthorities);
	AuthorityReference createTestUser(String username, AuthorityReference ... parentAuthorities);

	NodeRef createRootCategory(String categoryName);
	NodeRef createCategory(NodeRef parentCategory, String categoryName);

	NodeRef importFileFromClassPath(NodeRef parentRef, String fileName);
	void importView(NodeRef parentRef, String viewFileName, String messageFileName);

	SiteInfo createSite(String siteName, String siteTitle, String siteDescription, SiteVisibility siteVisibility);
}
