package fr.openwide.alfresco.demo.repo.service;

import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.InitializingBean;

import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.demo.business.model.DemoModel;
import fr.openwide.alfresco.repo.module.classification.service.ClassificationService;

public class DemoClassificationServiceImpl implements InitializingBean {

	private ClassificationService classificationService;
	private DemoClassificationPolicy policy;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		classificationService.addClassification(DemoModel.demoAspect, policy);
		
		classificationService.addClassification(CmModel.emailed)
			// On ne touche pas à ceux de moins de 24h
			.add(new RestrictionBuilder()
					.gt(CmModel.auditable.created, -24, ChronoUnit.HOURS).of(),
				builder -> {})
			// On classe dans /Email/2017_09/ ceux d'il y a moins d'un an
			.add(new RestrictionBuilder()
					.gt(CmModel.auditable.created, -1, ChronoUnit.YEARS).of(),
				builder -> builder.rootCompanyHome()
					.subFolder("Email")
					.subFolderDate("yyyy_MM"))
			// On archive dans /Email_archives/2017/09/ ceux crée il y a plus d'un an
			.add(new RestrictionBuilder()
					.gt(CmModel.auditable.created, -10, ChronoUnit.YEARS).of(),
				builder -> builder.rootCompanyHome()
					.subFolder("Email_archives")
					.subFolderYear()
					.subFolderMonth())
			// On efface ceux crée il y a plus de 10 ans
			.add(builder -> builder.delete());
	}

	public void setClassificationService(ClassificationService classificationService) {
		this.classificationService = classificationService;
	}
	public void setPolicy(DemoClassificationPolicy policy) {
		this.policy = policy;
	}
}