package fr.openwide.alfresco.demo.repo.service;

import org.alfresco.model.ContentModel;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.demo.business.model.DemoModel;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;
import fr.openwide.alfresco.repo.treeaspect.service.RegisterRootPropertyName;
import fr.openwide.alfresco.repo.treeaspect.service.TreeAspectService;

public class CopyAspect implements InitializingBean {

	@Autowired private TreeAspectService treeAspectService;

	@Autowired private RegisterRootPropertyName registerRootPropertyName;

	@Autowired private ConversionService conversionService;

	@Override public void afterPropertiesSet() throws Exception {
		treeAspectService.registerAspect(conversionService.getRequired(DemoModel.demoAspect.getNameReference()), false);
		registerRootPropertyName.registerCopyPropertyCmName(ContentModel.ASPECT_DUBLINCORE, QName.createQName("http://openwide.fr/modele/demo", "demoPropertyName"));
	}


}
