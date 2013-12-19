package fr.openwide.alfresco.component.model.framework.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fr.openwide.alfresco.app.core.framework.spring.config.AppCoreConfig;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.node.service.impl.NodeModelServiceImpl;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;
import fr.openwide.alfresco.component.model.search.service.impl.NodeSearchModelServiceImpl;

@Configuration
@Import(AppCoreConfig.class)
public class ComponentModelConfig {

	@Bean
	public NodeSearchModelService getNodeSearchServiceImpl() {
		return new NodeSearchModelServiceImpl();
	}

	@Bean
	public NodeModelService getNodeServiceImpl() {
		return new NodeModelServiceImpl();
	}
}
