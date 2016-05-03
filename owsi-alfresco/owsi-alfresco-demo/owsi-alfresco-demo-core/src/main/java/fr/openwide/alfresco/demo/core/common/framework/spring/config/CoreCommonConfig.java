package fr.openwide.alfresco.demo.core.common.framework.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import fr.openwide.alfresco.app.dictionary.framework.spring.config.AppDictionaryServiceConfig;
import fr.openwide.alfresco.app.module.framework.spring.config.AppModuleServiceConfig;
import fr.openwide.alfresco.demo.core.common.business.DemoBusinessPackage;

@Configuration
@Import({
	AppDictionaryServiceConfig.class,
	AppModuleServiceConfig.class
})
@ComponentScan(
	basePackageClasses = {
		DemoBusinessPackage.class
	},
	// https://jira.springsource.org/browse/SPR-8808
	// on veut charger de manière explicite le contexte ; de ce fait,
	// on ignore l'annotation @Configuration sur le scan de package.
	excludeFilters = {
		@Filter(Configuration.class)
	}
)
public class CoreCommonConfig {

	@Bean
	public static PropertySourcesPlaceholderConfigurer configurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}
