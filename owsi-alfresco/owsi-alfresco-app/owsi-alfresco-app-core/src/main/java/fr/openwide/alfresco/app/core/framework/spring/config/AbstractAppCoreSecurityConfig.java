package fr.openwide.alfresco.app.core.framework.spring.config;

import java.util.UUID;

import org.springframework.context.annotation.Bean;
import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;

public abstract class AbstractAppCoreSecurityConfig {

	private static final String RUN_AS_SHARED_KEY = UUID.randomUUID().toString();

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
