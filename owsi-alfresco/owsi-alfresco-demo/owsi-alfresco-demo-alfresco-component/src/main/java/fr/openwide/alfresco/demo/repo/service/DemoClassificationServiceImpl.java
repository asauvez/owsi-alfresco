package fr.openwide.alfresco.demo.repo.service;

import org.springframework.beans.factory.InitializingBean;

import fr.openwide.alfresco.demo.business.model.DemoModel;
import fr.openwide.alfresco.repo.module.classification.service.ClassificationService;

public class DemoClassificationServiceImpl implements InitializingBean {

	private ClassificationService classificationService;
	private DemoClassificationPolicy policy;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		classificationService.addClassification(DemoModel.demoAspect, policy);
	}

	public void setClassificationService(ClassificationService classificationService) {
		this.classificationService = classificationService;
	}
	public void setPolicy(DemoClassificationPolicy policy) {
		this.policy = policy;
	}
}