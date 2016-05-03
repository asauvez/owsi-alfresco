package fr.openwide.alfresco.demo.repo.patch;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.demo.business.model.DemoModel;
import fr.openwide.alfresco.repo.module.bootstrap.patch.AbstractBootstrapPatch;

public class DemoPatch extends AbstractBootstrapPatch {

	@Override
	protected void executeInternal() throws Throwable {
		NodeReference companyHome = nodeModelService.getCompanyHome();
		NodeReference demoFolder = nodeModelService.createFolder(companyHome, "Demo");
		identificationService.setIdentifier(demoFolder, DemoModel.DEMO_ROOT_FOLDER);
	}

}