package fr.openwide.alfresco.app.core.framework.spring.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;

import fr.openwide.alfresco.app.core.security.service.RunAsUserManager;
import fr.openwide.alfresco.app.core.security.service.UserService;
import fr.openwide.alfresco.app.core.security.service.impl.RunAsUserManagerImpl;
import fr.openwide.alfresco.app.core.security.service.impl.UserServiceImpl;

@Configuration
public class UserDetailsAppCoreSecurityConfig extends AbstractAppCoreSecurityConfig {

	@Autowired
	@Qualifier("runAsUserDetailsService")
	private UserDetailsService userDetailsService;

	@Bean
	public UserService userService() {
		return new UserServiceImpl();
	}

	@Bean
	public RunAsUserManager runAsUserManager(AuthenticationManager authenticationManager) {
		RunAsUserManagerImpl manager = new RunAsUserManagerImpl(authenticationManager, userService());
		manager.setUserDetailsService(userDetailsService);
		manager.setKey(runAsSharedKey());
		return manager;
	}

}
