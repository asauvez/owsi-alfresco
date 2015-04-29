package fr.openwide.alfresco.component.model.framework.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fr.openwide.alfresco.app.core.framework.spring.config.AppCoreRemoteBindingConfig;
import fr.openwide.alfresco.component.model.authority.service.AuthorityModelService;
import fr.openwide.alfresco.component.model.authority.service.impl.AuthorityModelServiceImpl;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.node.service.impl.NodeModelServiceImpl;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;
import fr.openwide.alfresco.component.model.search.service.impl.NodeSearchModelServiceImpl;

@Configuration
@Import(AppCoreRemoteBindingConfig.class)
public class ComponentModelConfig {

	@Bean
	public NodeModelService nodeModelService() {
		return new NodeModelServiceImpl();
	}

	@Bean
	public NodeSearchModelService nodeSearchModelService() {
		return new NodeSearchModelServiceImpl();
	}

	@Bean
	public AuthorityModelService authorityModelService() {
		return new AuthorityModelServiceImpl();
	}
}
