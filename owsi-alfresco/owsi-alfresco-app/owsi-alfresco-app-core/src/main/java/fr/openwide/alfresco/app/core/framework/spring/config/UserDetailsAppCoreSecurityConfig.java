package fr.openwide.alfresco.app.core.framework.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import fr.openwide.alfresco.app.core.security.service.RepositoryAuthenticationUserDetailsService;
import fr.openwide.alfresco.app.core.security.service.RepositoryTicketProvider;
import fr.openwide.alfresco.app.core.security.service.RunAsUserManager;
import fr.openwide.alfresco.app.core.security.service.UserService;
import fr.openwide.alfresco.app.core.security.service.impl.RunAsUserManagerImpl;
import fr.openwide.alfresco.app.core.security.service.impl.UserAwareRepositoryTicketProvider;
import fr.openwide.alfresco.app.core.security.service.impl.UserServiceImpl;

@Configuration
public class UserDetailsAppCoreSecurityConfig extends AbstractAppCoreSecurityConfig {

	@Autowired
	@Qualifier("runAsUserDetailsService")
	private RepositoryAuthenticationUserDetailsService userDetailsService;

	@Bean
	public UserService userService() {
		return new UserServiceImpl();
	}

	@Bean
	public RepositoryTicketProvider ticketProvider() {
		return new UserAwareRepositoryTicketProvider(userService(), userDetailsService);
	}

	@Bean
	public RunAsUserManager runAsUserManager(AuthenticationManager authenticationManager) {
		RunAsUserManagerImpl manager = new RunAsUserManagerImpl(authenticationManager, userService());
		manager.setUserDetailsService(userDetailsService);
		manager.setKey(runAsSharedKey());
		return manager;
	}

}
