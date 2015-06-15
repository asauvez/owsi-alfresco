package fr.openwide.alfresco.app.core.security.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import fr.openwide.alfresco.app.core.security.service.UserService;

public class RunAsUserManagerImpl extends AbstractRunAsUserManagerImpl {

	private UserDetailsService userDetailsService;

	public RunAsUserManagerImpl(AuthenticationManager authenticationManager, UserService userService) {
		super(authenticationManager, userService);
	}

	@Override
	protected UserDetails loadUserDetails(String username) {
		return userDetailsService.loadUserByUsername(username);
	}

	@Override
	protected void afterRunAs(Authentication runAsAuthentication) {
		// not needed
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

}
