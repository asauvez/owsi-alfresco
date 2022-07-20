package fr.openwide.alfresco.demo;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.repo.module.classification.service.ClassificationService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateService;

/**
 * Implemente une classification manuelle :
 * Les utilisateurs peuvent créer à la main des dossiers du type :
 * /Sites/demo/documentLibrary/treeAspect/<region>/<client>/facture.pdf
 * 
 * La facture hérite des metas treeAspectRegionName et treeAspectClientName, qui valent le nom du dossier région et client.
 * 
 * @author adrsau
 */
@GenerateService
public class DemoTreeAspectPolicy implements InitializingBean {
	
	@Autowired private ClassificationService classificationService;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// Tous les dossiers crées dans /Sites/demo/documentLibrary/treeAspect/ prennent automatiquement l'aspect RegionFolder
		classificationService.registerChildAspectForFolder(DemoModel.treeAspectRootFolder, DemoModel.treeAspectRegionFolder);
		// Tous les dossiers crées dans un dossier RegionFolder prennent automatiquement l'aspect ClientFolder
		classificationService.registerChildAspectForFolder(DemoModel.treeAspectRegionFolder, DemoModel.treeAspectClientFolder);
		// Tous les documents crées dans un dossier ClientFolder prennent automatiquement l'aspect FactureDocument
		classificationService.registerChildAspectForContent(DemoModel.treeAspectClientFolder, DemoModel.treeAspectFactureDocument);

		// Les metas de RegionInfo sont héritées automatiquement.
		classificationService.registerTreeAspect(DemoModel.treeAspectRegionInfo);
		// Les metas de ClientInfo sont héritées automatiquement.
		classificationService.registerTreeAspect(DemoModel.treeAspectClientInfo);
		
		// Le nom d'un RegionFolder est copié automatiquement dans treeAspectRegionName
		classificationService.registerCopyPropertyCmName(DemoModel.treeAspectRegionFolder, DemoModel.treeAspectRegionFolder.treeAspectRegionInfo.treeAspectRegionName);
		// Le nom d'un ClientFolder est copié automatiquement dans treeAspectClientName
		classificationService.registerCopyPropertyCmName(DemoModel.treeAspectClientFolder, DemoModel.treeAspectClientFolder.treeAspectClientInfo.treeAspectClientName);
	}
}