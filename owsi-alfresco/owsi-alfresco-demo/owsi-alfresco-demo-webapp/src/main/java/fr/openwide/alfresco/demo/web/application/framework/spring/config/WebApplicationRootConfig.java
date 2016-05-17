package fr.openwide.alfresco.demo.web.application.framework.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import fr.openwide.alfresco.demo.core.framework.spring.config.CoreCommonConfig;

@Configuration
@PropertySources({
	@PropertySource({
		"classpath:owsi-core-component-spring.properties",
		"classpath:owsi-core-component-jpa.properties",
		"classpath:owsi-alfresco-app-core.properties",
		"classpath:core-common.properties",
		"classpath:${application.name}-global.properties"
	}),
	@PropertySource(
		value = "classpath:${application.name}-${user.name}.properties",
		ignoreResourceNotFound = true
	)
})
@Import({
	CoreCommonConfig.class,
	WebApplicationSecurityConfig.class
})
public class WebApplicationRootConfig {
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer configurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
}
