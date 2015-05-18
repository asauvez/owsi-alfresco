package fr.openwide.alfresco.app.dictionary.framework.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fr.openwide.alfresco.app.core.authority.service.AuthorityService;
import fr.openwide.alfresco.app.core.framework.spring.config.AppCoreServiceConfig;
import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.app.core.search.service.NodeSearchService;
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
	public NodeModelService nodeModelService(NodeService nodeService) {
		return new NodeModelServiceImpl(nodeService);
	}

	@Bean
	public NodeSearchModelService nodeSearchModelService(NodeSearchService nodeSearchService) {
		return new NodeSearchModelServiceImpl(nodeSearchService);
	}

	@Bean
	public final AuthorityModelService authorityModelService(AuthorityService authorityService) {
		return new AuthorityModelServiceImpl(authorityService);
	}

}
