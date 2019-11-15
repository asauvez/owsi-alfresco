package fr.openwide.alfresco.demo.repo.service;

import fr.openwide.alfresco.component.model.repository.model.cm.CmObject;
import fr.openwide.alfresco.demo.business.model.DemoModel;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;
import fr.openwide.alfresco.repo.treeaspect.service.RegisterRootPropertyName;
import fr.openwide.alfresco.repo.treeaspect.service.TreeAspectService;
import org.alfresco.model.ContentModel;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public class CopyAspect implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(CopyAspect.class);

	@Autowired private TreeAspectService treeAspectService;

	@Autowired private RegisterRootPropertyName registerRootPropertyName;

	@Autowired private ConversionService conversionService;

	@Override public void afterPropertiesSet() throws Exception {
		treeAspectService.registerAspect(conversionService.getRequired(DemoModel.demoAspect.getNameReference()), false);
		registerRootPropertyName.registerCopyPropertyName(ContentModel.ASPECT_DUBLINCORE, QName.createQName("http://openwide.fr/modele/demo", "demoPropertyName"));
	}


}
