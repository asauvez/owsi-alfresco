package fr.openwide.alfresco.demo.repo.patch;

import java.util.Optional;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.demo.business.model.DemoModel;
import fr.openwide.alfresco.repo.module.bootstrap.patch.AbstractBootstrapPatch;

public class DemoPatch extends AbstractBootstrapPatch {

	private static final String DEMO_FOLDER_NAME = "Demo";

	@Override
	protected void executeInternal() throws Throwable {
		NodeReference companyHome = nodeModelService.getCompanyHome();
		Optional<NodeReference> demoFolder = nodeModelService.getChildByName(companyHome, DEMO_FOLDER_NAME);
		if (! demoFolder.isPresent()) {
			demoFolder = Optional.of(nodeModelService.createFolder(companyHome, DEMO_FOLDER_NAME));
		}
		identificationService.setIdentifier(demoFolder.get(), DemoModel.DEMO_ROOT_FOLDER);
	}

}