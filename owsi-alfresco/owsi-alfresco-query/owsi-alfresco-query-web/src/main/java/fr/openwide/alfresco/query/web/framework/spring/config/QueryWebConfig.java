package fr.openwide.alfresco.query.web.framework.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fr.openwide.alfresco.query.core.framework.spring.config.QueryCoreConfig;
import fr.openwide.alfresco.query.web.search.service.NodeFormQueryService;
import fr.openwide.alfresco.query.web.search.service.impl.NodeFormQueryServiceImpl;

@Configuration
@Import(QueryCoreConfig.class)
public class QueryWebConfig {

	@Bean
	public NodeFormQueryService getNodeFormQueryService() {
		return new NodeFormQueryServiceImpl();
	}
}
