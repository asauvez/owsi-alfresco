package fr.openwide.alfresco.demo;

import org.alfresco.service.cmr.site.SiteRole;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.repo.core.bootstrap.builder.RandomFileGenerator;
import fr.openwide.alfresco.repo.core.bootstrap.service.BootstrapService;
import fr.openwide.alfresco.repo.core.bootstrap.service.RunAtEveryLaunchPatch;
import fr.openwide.alfresco.repo.module.OwsiModel;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GeneratePatch;

@GeneratePatch
public class DemoBootstrap extends RunAtEveryLaunchPatch {

	public static final String SITE_NAME = "demo";
	@Autowired private BootstrapService bootstrapService;
	
	@Override
	protected String applyInternal() throws Exception {
		AuthorityReference demoUser = bootstrapService.getOrCreateUser("demo", "Utilisateur", "Demo", null, "demo");
		
		bootstrapService.getOrCreateSite(SITE_NAME, "demo", "", SiteVisibility.PRIVATE)
			.membership(demoUser, SiteRole.SiteCollaborator)
			.documentLibrary()
				.subFolder("Depot")
					.importFileFromClassPath("/alfresco/module/owsi-alfresco-demo-ged-platform/module.properties")
						.parent()
					.generateRandomFiles(new RandomFileGenerator(100)
						.applyToFolder(folder -> folder.aspect(OwsiModel.deleteIfEmpty)))
					.parent()
				.subFolder("treeAspect")
					.aspect(DemoModel.treeAspectRootFolder)
					.parent();
		
		bootstrapService.getDataDictionary()
			.subFolder("demo")
				.importFileFromClassPath("/alfresco/module/owsi-alfresco-demo-ged-platform/module.properties")
					.parent()
				.parent();
		
		return "success";
	}
}
