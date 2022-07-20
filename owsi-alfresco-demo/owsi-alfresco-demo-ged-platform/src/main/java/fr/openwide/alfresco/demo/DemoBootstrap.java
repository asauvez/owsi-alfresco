package fr.openwide.alfresco.demo;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteRole;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.repo.core.bootstrap.service.BootstrapService;
import fr.openwide.alfresco.repo.core.bootstrap.service.RunAtEveryLaunchPatch;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GeneratePatch;

@GeneratePatch
public class DemoBootstrap extends RunAtEveryLaunchPatch {

	public static final String SITE_NAME = "demo";
	@Autowired private BootstrapService bootstrapService;
	
	@Override
	protected String applyInternal() throws Exception {
		AuthorityReference demoUser = bootstrapService.getOrCreateUser("demo", "Utilisateur", "Demo", null, "demo");
		SiteInfo siteInfo = bootstrapService.getOrCreateSite(SITE_NAME, "demo", "", SiteVisibility.PRIVATE);
		bootstrapService.setSiteMembership(siteInfo, demoUser, SiteRole.SiteCollaborator);
		NodeRef documentLibrary = bootstrapService.getOrCreateDocumentLibrary(siteInfo);
		
		bootstrapService.getOrCreateFolder(documentLibrary, "treeAspect", DemoModel.treeAspectRootFolder);
		
		return "success";
	}
}
