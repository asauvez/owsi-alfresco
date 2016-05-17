package fr.openwide.alfresco.demo.core.framework.spring.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;
import org.springframework.security.acls.domain.PermissionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;

import fr.openwide.alfresco.app.core.authentication.service.AuthenticationService;
import fr.openwide.alfresco.app.core.framework.spring.config.AppCorePermissionConfigurerAdapter;
import fr.openwide.alfresco.app.core.framework.spring.config.EnableAppCoreSecurity;
import fr.openwide.alfresco.app.core.security.service.RepositoryAuthenticationUserDetailsService;
import fr.openwide.alfresco.app.core.security.service.impl.RepositoryAuthenticationProvider;
import fr.openwide.alfresco.app.core.security.service.impl.RepositoryAuthenticationUserDetailsServiceImpl;
import fr.openwide.alfresco.demo.core.application.security.service.impl.BusinessPermissionEvaluator;
import fr.openwide.core.jpa.security.model.NamedPermission;
import fr.openwide.core.jpa.security.service.NamedPermissionFactory;

@Configuration
@EnableAppCoreSecurity
public class CoreCommonSecurityConfig extends AppCorePermissionConfigurerAdapter {

	@Bean
	public RepositoryAuthenticationProvider repositoryAuthenticationProvider() {
		return new RepositoryAuthenticationProvider();
	}

	@Bean(name = {"repositoryAuthenticationUserDetailsService", "preAuthenticatedUserDetailsService"})
	public RepositoryAuthenticationUserDetailsService userDetailsService(AuthenticationService authenticationService) {
		return new RepositoryAuthenticationUserDetailsServiceImpl(authenticationService, loginTimeRoleHierarchy());
	}

	@Override
	public PermissionEvaluator applicationPermissionEvaluator() {
		return new BusinessPermissionEvaluator();
	}

	@Override
	public PermissionFactory applicationPermissionFactory() {
		return new NamedPermissionFactory(NamedPermission.class);
	}

}
