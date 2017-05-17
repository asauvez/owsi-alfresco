package fr.openwide.alfresco.app.core.security.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;

import fr.openwide.alfresco.app.core.security.service.RepositoryAuthenticationUserDetailsService;
import fr.openwide.alfresco.app.core.security.service.UserService;

public class RepositoryRunAsUserManagerImpl extends RunAsUserManagerImpl {

	private RepositoryAuthenticationUserDetailsService repositoryAuthenticationUserDetailsService;

	public RepositoryRunAsUserManagerImpl(
			AuthenticationManager authenticationManager, 
			RepositoryAuthenticationUserDetailsService repositoryAuthenticationUserDetailsService, 
			UserService userService) {
		super(authenticationManager, repositoryAuthenticationUserDetailsService, userService);
	}

	@Override
	protected void afterRunAs(Authentication runAsAuthentication) {
		// Logout runAs ticket
		repositoryAuthenticationUserDetailsService.logout(runAsAuthentication);
	}

}
