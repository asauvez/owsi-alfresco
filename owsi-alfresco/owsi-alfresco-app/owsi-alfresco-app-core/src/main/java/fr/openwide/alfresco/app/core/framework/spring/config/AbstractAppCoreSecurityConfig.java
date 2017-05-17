package fr.openwide.alfresco.app.core.framework.spring.config;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationManager;

import fr.openwide.alfresco.app.core.authentication.service.AuthenticationService;
import fr.openwide.alfresco.app.core.security.service.RepositoryAuthenticationUserDetailsService;
import fr.openwide.alfresco.app.core.security.service.RepositoryTicketProvider;
import fr.openwide.alfresco.app.core.security.service.RunAsUserManager;
import fr.openwide.alfresco.app.core.security.service.UserService;
import fr.openwide.alfresco.app.core.security.service.impl.RepositoryAuthenticationUserDetailsServiceImpl;

public abstract class AbstractAppCoreSecurityConfig {

	private static final String RUN_AS_SHARED_KEY = UUID.randomUUID().toString();

	public abstract UserService userService();
	public abstract RepositoryTicketProvider ticketProvider();
	public abstract RunAsUserManager runAsUserManager(AuthenticationManager authenticationManager);
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired(required=false)
	@Qualifier(AppCorePermissionConfigurerAdapter.LOGIN_TIME_ROLE_HIERARCHY)
	public RoleHierarchy loginTimeRoleHierarchy;
	
	@Bean
	public RepositoryAuthenticationUserDetailsService repositoryAuthenticationUserDetailsService() {
		return new RepositoryAuthenticationUserDetailsServiceImpl(authenticationService, loginTimeRoleHierarchy);
	}

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
