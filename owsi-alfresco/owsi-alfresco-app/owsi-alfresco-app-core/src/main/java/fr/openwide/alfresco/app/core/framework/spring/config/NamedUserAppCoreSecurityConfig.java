package fr.openwide.alfresco.app.core.framework.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.openwide.alfresco.app.core.security.service.impl.RepositoryAuthenticationProvider;

/**
 * Config for PrincipalType.NAMED_USER.
 * 
 * Alfresco is the main authentication mecanism.
 */
@Configuration
public class NamedUserAppCoreSecurityConfig extends AbstractAppCoreSecurityConfig {

	/**
	 * Provider to allow Spring MVC to authenticate with Alfresco.
	 */
	@Bean
	public RepositoryAuthenticationProvider repositoryAuthenticationProvider() {
		return new RepositoryAuthenticationProvider();
	}
}
