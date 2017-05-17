package fr.openwide.alfresco.app.core.framework.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import fr.openwide.alfresco.app.core.security.service.NamedUserService;
import fr.openwide.alfresco.app.core.security.service.RepositoryAuthenticationUserDetailsService;
import fr.openwide.alfresco.app.core.security.service.RepositoryTicketProvider;
import fr.openwide.alfresco.app.core.security.service.RunAsUserManager;
import fr.openwide.alfresco.app.core.security.service.impl.NamedUserServiceImpl;
import fr.openwide.alfresco.app.core.security.service.impl.RepositoryRunAsUserManagerImpl;

/**
 * Config for PrincipalType.NAMED_USER.
 * 
 * Alfresco is the main authentication mecanism.
 */
@Configuration
public class NamedUserAppCoreSecurityConfig extends AbstractAppCoreSecurityConfig {

	/**
	 * Instantiated by application in xxx.CoreCommonSecurityConfig.
	 * 
	 * TODO: instantiate it here. Manage loginTimeRoleHierarchy.
	 */
	@Autowired
	private RepositoryAuthenticationUserDetailsService repositoryAuthenticationUserDetailsService;

	/**
	 * Returns info about the current user.
	 * The principal is always a NamedUser.
	 */
	@Bean
	@Override
	public NamedUserService userService() {
		return new NamedUserServiceImpl();
	}
	
	@Bean
	@Override
	public RepositoryTicketProvider ticketProvider() {
		return new RepositoryTicketProvider(userService(), repositoryAuthenticationUserDetailsService);
	}

	@Bean
	@Override
	public RunAsUserManager runAsUserManager(AuthenticationManager authenticationManager) {
		RepositoryRunAsUserManagerImpl manager = new RepositoryRunAsUserManagerImpl(
				authenticationManager, 
				repositoryAuthenticationUserDetailsService, 
				userService());
		manager.setKey(runAsSharedKey());
		return manager;
	}

}
