package fr.openwide.alfresco.app.core.security.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Authentication provider needed to validate {@link UsernamePasswordAuthenticationToken} tokens containing username and
 * password against the repository
 */
public class RepositoryAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	@Autowired
	private AuthenticationUserDetailsService<UsernamePasswordAuthenticationToken> authenticationUserDetailsService;

	@Override
	public UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) {
		return authenticationUserDetailsService.loadUserDetails(authentication);
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
		// not needed
	}

}
