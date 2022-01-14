package fr.openwide.alfresco.demo.repo.patch;

import java.util.Optional;

import org.alfresco.service.cmr.repository.NodeRef;

import fr.openwide.alfresco.demo.business.model.DemoModel;
import fr.openwide.alfresco.repo.module.bootstrap.patch.AbstractBootstrapPatch;

//@GeneratePatch(dependsOn = {"owsi.service.bootstrapService"})
public class DemoPatch extends AbstractBootstrapPatch {

	private static final String DEMO_FOLDER_NAME = "Demo";

	@Override
	protected void executeInternal() throws Throwable {
		NodeRef companyHome = nodeModelService.getCompanyHome();
		Optional<NodeRef> demoFolder = nodeModelService.getChildByName(companyHome, DEMO_FOLDER_NAME);
		if (! demoFolder.isPresent()) {
			demoFolder = Optional.of(nodeModelService.createFolder(companyHome, DEMO_FOLDER_NAME));
		}
		identificationService.setIdentifier(demoFolder.get(), DemoModel.DEMO_ROOT_FOLDER);
	}

}