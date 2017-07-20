package fr.openwide.alfresco.app.dictionary.framework.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService;
import fr.openwide.alfresco.api.core.search.service.NodeSearchRemoteService;
import fr.openwide.alfresco.app.core.framework.spring.config.AppCoreServiceConfig;
import fr.openwide.alfresco.component.model.authority.service.AuthorityModelService;
import fr.openwide.alfresco.component.model.authority.service.impl.AuthorityModelServiceImpl;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.node.service.impl.NodeModelServiceImpl;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;
import fr.openwide.alfresco.component.model.search.service.impl.NodeSearchModelServiceImpl;

@Configuration
@Import(AppCoreServiceConfig.class)
public class AppDictionaryServiceConfig {

	@Bean
	public NodeModelService nodeModelService(NodeRemoteService nodeService) {
		return new NodeModelServiceImpl(nodeService);
	}

	@Bean
	public NodeSearchModelService nodeSearchModelService(NodeSearchRemoteService nodeSearchService) {
		return new NodeSearchModelServiceImpl(nodeSearchService);
	}

	@Bean
	public AuthorityModelService authorityModelService(AuthorityRemoteService authorityService) {
		return new AuthorityModelServiceImpl(authorityService);
	}

}
