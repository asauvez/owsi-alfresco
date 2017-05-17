package fr.openwide.alfresco.app.core.framework.spring.config;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;

import fr.openwide.alfresco.app.core.security.service.RunAsUserManager;
import fr.openwide.alfresco.app.core.security.service.UserService;

public abstract class AbstractAppCoreSecurityConfig {

	private static final String RUN_AS_SHARED_KEY = UUID.randomUUID().toString();

	public abstract UserService userService();
	public abstract RunAsUserManager runAsUserManager(AuthenticationManager authenticationManager);
	
	@Bean
	public RunAsImplAuthenticationProvider runAsAuthenticationProvider() {
		RunAsImplAuthenticationProvider provider = new RunAsImplAuthenticationProvider();
		provider.setKey(runAsSharedKey());
		return provider;
	}

	protected String runAsSharedKey() {
		return RUN_AS_SHARED_KEY;
	}

}
