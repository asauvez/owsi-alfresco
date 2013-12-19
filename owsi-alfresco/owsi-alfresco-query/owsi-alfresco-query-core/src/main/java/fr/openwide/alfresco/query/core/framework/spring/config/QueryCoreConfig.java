package fr.openwide.alfresco.query.core.framework.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fr.openwide.alfresco.app.core.framework.spring.config.AppCoreConfig;
import fr.openwide.alfresco.query.core.search.service.NodeSearchService;
import fr.openwide.alfresco.query.core.search.service.impl.NodeSearchServiceImpl;

@Configuration
@Import(AppCoreConfig.class)
public class QueryCoreConfig {

	@Bean
	public NodeSearchService getNodeSearchServiceImpl() {
		return new NodeSearchServiceImpl();
	}
}
