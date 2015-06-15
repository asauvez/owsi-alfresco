package fr.openwide.alfresco.app.core.security.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import fr.openwide.alfresco.app.core.security.service.RepositoryAuthenticationUserDetailsService;
import fr.openwide.alfresco.app.core.security.service.UserService;

public class RepositoryRunAsUserManagerImpl extends AbstractRunAsUserManagerImpl {

	private RepositoryAuthenticationUserDetailsService repositoryAuthenticationUserDetailsService;

	public RepositoryRunAsUserManagerImpl(AuthenticationManager authenticationManager, UserService userService) {
		super(authenticationManager, userService);
	}

	@Override
	protected UserDetails loadUserDetails(String username) {
		return repositoryAuthenticationUserDetailsService.loadUserByUsername(username);
	}

	@Override
	protected void afterRunAs(Authentication runAsAuthentication) {
		// Logout runAs ticket
		repositoryAuthenticationUserDetailsService.logout(runAsAuthentication);
	}

	public void setRepositoryAuthenticationUserDetailsService(RepositoryAuthenticationUserDetailsService repositoryAuthenticationUserDetailsService) {
		this.repositoryAuthenticationUserDetailsService = repositoryAuthenticationUserDetailsService;
	}

}
