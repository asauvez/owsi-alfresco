package fr.openwide.alfresco.app.core.framework.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.openwide.alfresco.app.core.security.service.UserService;
import fr.openwide.alfresco.app.core.security.service.impl.UserServiceImpl;

/**
 * Config for PrincipalType.USER_DETAILS.
 *  
 * The main authentication mecanism is not Alfresco. It may be users in database or any other systems.
 * 
 * To call Alfresco, you have to be in a runAs bloc. When the runAs bloc is ended, the original authentication
 * context is restored.
 */
@Configuration
public class UserDetailsAppCoreSecurityConfig extends AbstractAppCoreSecurityConfig {

	/**
	 * Returns info about the current user.
	 * 
	 * The principal is a NamedUser when we are inside a runAs.
	 * Outside runAs, Principal is used to evaluate permission.
	 */
	@Bean
	@Override
	public UserService userService() {
		return new UserServiceImpl();
	}

	@Override
	public boolean logoutAfterRunAs() {
		return false;
	}
}
