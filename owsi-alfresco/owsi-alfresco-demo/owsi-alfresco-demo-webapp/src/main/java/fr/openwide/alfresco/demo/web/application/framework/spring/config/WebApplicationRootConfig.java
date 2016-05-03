package fr.openwide.alfresco.demo.web.application.framework.spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import fr.openwide.alfresco.demo.core.common.framework.spring.config.CoreCommonConfig;

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
})
public class WebApplicationRootConfig {

}
