package fr.openwide.alfresco.app.core.framework.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.openwide.alfresco.app.core.security.service.NamedUserService;
import fr.openwide.alfresco.app.core.security.service.impl.NamedUserServiceImpl;
import fr.openwide.alfresco.app.core.security.service.impl.RepositoryAuthenticationProvider;

/**
 * Config for PrincipalType.NAMED_USER.
 * 
 * Alfresco is the main authentication mecanism.
 */
@Configuration
public class NamedUserAppCoreSecurityConfig extends AbstractAppCoreSecurityConfig {

	/**
	 * Returns info about the current user.
	 * 
	 * The principal is always a NamedUser.
	 */
	@Bean
	@Override
	public NamedUserService userService() {
		return new NamedUserServiceImpl();
	}

	@Override
	public boolean logoutAfterRunAs() {
		return true;
	}

	/**
	 * Provider to allow Spring MVC to authenticate with Alfresco.
	 */
	@Bean
	public RepositoryAuthenticationProvider repositoryAuthenticationProvider() {
		return new RepositoryAuthenticationProvider();
	}
}
