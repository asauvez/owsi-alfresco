package fr.openwide.alfresco.app.module.framework.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fr.openwide.alfresco.api.module.identification.service.IdentificationService;
import fr.openwide.alfresco.api.module.identification.service.impl.IdentificationServiceImpl;
import fr.openwide.alfresco.app.dictionary.framework.spring.config.AppDictionaryServiceConfig;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;

@Configuration
@Import(AppDictionaryServiceConfig.class)
public class AppModuleServiceConfig {

	@Bean
	public IdentificationService identificationService(NodeSearchModelService nodeSearchModelService, NodeModelService nodeModelService) {
		return new IdentificationServiceImpl(nodeSearchModelService, nodeModelService);
	}

}
