package fr.openwide.alfresco.app.core.security.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import fr.openwide.alfresco.app.core.authentication.model.RepositoryTicketProvider;
import fr.openwide.alfresco.app.core.authentication.service.AuthenticationService;
import fr.openwide.alfresco.app.core.remote.model.RepositoryConnectException;
import fr.openwide.alfresco.app.core.security.model.NamedUser;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryUser;
import fr.openwide.alfresco.repository.api.node.model.RepositoryAuthority;
import fr.openwide.alfresco.repository.api.remote.exception.AccessDeniedRemoteException;
import fr.openwide.core.jpa.security.business.authority.util.CoreAuthorityConstants;

@Component
public class RepositoryAuthenticationUserDetailsService implements AuthenticationUserDetailsService<UsernamePasswordAuthenticationToken>, UserDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryAuthenticationUserDetailsService.class);

	@Autowired
	private AuthenticationService authenticationService;

	@Override
	public UserDetails loadUserDetails(UsernamePasswordAuthenticationToken token) throws UsernameNotFoundException {
		String username = token != null ? (String) token.getName() : null;
		String credentials = token != null ? (String) token.getCredentials() : null;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Authenticating user with '{}' password: {}", 
					Strings.isNullOrEmpty(credentials) ? String.valueOf(credentials) : "[PROTECTED]", username);
		}
		try {
			RepositoryUser repositoryUser = authenticationService.authenticate(username, credentials);
			return buildUserDetails(repositoryUser, credentials);
		} catch (RepositoryConnectException e) {
			throw new AuthenticationServiceException("Could not connect to repository", e);
		} catch (AccessDeniedRemoteException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Could not authenticate user: " + username, e);
			}
			throw new BadCredentialsException("Could not authenticate user: " + username, e);
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Authenticating user: {}", username);
		}
		try {
			RepositoryUser repositoryUser = authenticationService.authenticate(username);
			return buildUserDetails(repositoryUser, null);
		} catch (RepositoryConnectException e) {
			throw new AuthenticationServiceException("Could not connect to repository", e);
		} catch (AccessDeniedRemoteException e) {
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Could not authenticate user: " + username, e);
			}
			throw new AuthenticationServiceException("Could not authenticate user: " + username, e);
		}
	}

	private UserDetails buildUserDetails(RepositoryUser repositoryUser, String credentials) {
		// Build authority list
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		// Check for Admin role
		if (repositoryUser.isAdmin()) {
			authorities.add(CoreAuthorityConstants.AUTHORITY_ADMIN);
		}
		// Add default Authenticated role
		authorities.add(new SimpleGrantedAuthority(CoreAuthorityConstants.ROLE_AUTHENTICATED));
		for (RepositoryAuthority autority : repositoryUser.getAuthorities()) {
			authorities.add(new SimpleGrantedAuthority(autority.getName()));
		}
		// Build user
		return new NamedUser(repositoryUser, credentials, authorities);
	}

	public void logout(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		if (principal instanceof RepositoryTicketProvider) {
			RepositoryTicketProvider user = (RepositoryTicketProvider) principal;
			authenticationService.logout(user.getTicket());
		} else {
			throw new IllegalStateException("Invalid authentication principal: " + principal);
		}
	}

}
