package fr.openwide.alfresco.app.core.framework.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;

import fr.openwide.alfresco.app.core.security.service.NamedUserService;
import fr.openwide.alfresco.app.core.security.service.RepositoryAuthenticationUserDetailsService;
import fr.openwide.alfresco.app.core.security.service.RunAsUserManager;
import fr.openwide.alfresco.app.core.security.service.impl.NamedUserServiceImpl;
import fr.openwide.alfresco.app.core.security.service.impl.RepositoryRunAsUserManagerImpl;

@Configuration
public class NamedUserAppCoreSecurityConfig extends AbstractAppCoreSecurityConfig {

	@Autowired
	private RepositoryAuthenticationUserDetailsService repositoryAuthenticationUserDetailsService;

	@Bean
	public NamedUserService userService() {
		return new NamedUserServiceImpl(repositoryAuthenticationUserDetailsService);
	}

	@Bean
	public RunAsUserManager runAsUserManager(AuthenticationManager authenticationManager) {
		RepositoryRunAsUserManagerImpl manager = new RepositoryRunAsUserManagerImpl(authenticationManager, userService());
		manager.setRepositoryAuthenticationUserDetailsService(repositoryAuthenticationUserDetailsService);
		manager.setKey(runAsSharedKey());
		return manager;
	}

}
