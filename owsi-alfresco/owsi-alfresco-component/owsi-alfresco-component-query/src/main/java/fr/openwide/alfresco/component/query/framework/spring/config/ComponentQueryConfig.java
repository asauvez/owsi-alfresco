package fr.openwide.alfresco.component.query.framework.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import fr.openwide.alfresco.app.core.framework.spring.config.AppCoreRemoteBindingConfig;
import fr.openwide.alfresco.app.dictionary.framework.spring.config.AppDictionaryServiceConfig;
import fr.openwide.alfresco.component.query.search.service.BeanFormQueryService;
import fr.openwide.alfresco.component.query.search.service.NodeFormQueryService;
import fr.openwide.alfresco.component.query.search.service.impl.BeanFormQueryServiceImpl;
import fr.openwide.alfresco.component.query.search.service.impl.NodeFormQueryServiceImpl;

@Configuration
@Import({
	AppCoreRemoteBindingConfig.class,
	AppDictionaryServiceConfig.class
})
public class ComponentQueryConfig {

	@Bean
	public NodeFormQueryService getNodeFormQueryService() {
		return new NodeFormQueryServiceImpl();
	}

	@Bean
	public BeanFormQueryService getBeanFormQueryService() {
		return new BeanFormQueryServiceImpl();
	}

}
