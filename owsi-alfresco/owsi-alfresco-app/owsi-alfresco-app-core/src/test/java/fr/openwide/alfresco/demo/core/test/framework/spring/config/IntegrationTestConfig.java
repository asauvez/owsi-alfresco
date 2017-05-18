package fr.openwide.alfresco.demo.core.test.framework.spring.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;

import fr.openwide.alfresco.app.core.framework.spring.config.AppCoreServiceConfig;
import fr.openwide.alfresco.app.core.framework.spring.config.EnableAppCoreSecurity;

@Configuration
@PropertySources({
	@PropertySource({
		"classpath:owsi-core-component-spring.properties",
		"classpath:owsi-core-component-jpa.properties",
		"classpath:owsi-alfresco-app-core.properties",
		"classpath:core-common.properties",
		"classpath:tests.properties"
	}),
})
@Import(AppCoreServiceConfig.class)
@EnableAppCoreSecurity()
public abstract class IntegrationTestConfig {

	@Bean
	public AuthenticationManager authenticationManager(RunAsImplAuthenticationProvider provider) {
		return new ProviderManager(Arrays.asList((AuthenticationProvider) provider));
	}
}
