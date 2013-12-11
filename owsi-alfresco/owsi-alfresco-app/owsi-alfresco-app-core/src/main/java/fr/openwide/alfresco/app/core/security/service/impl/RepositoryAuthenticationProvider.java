package fr.openwide.alfresco.app.core.security.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import fr.openwide.alfresco.app.core.authentication.service.AuthenticationService;
import fr.openwide.alfresco.app.core.security.model.BusinessUser;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryUser;
import fr.openwide.alfresco.repository.api.remote.model.RepositoryRemoteException;
import fr.openwide.core.jpa.exception.SecurityServiceException;
import fr.openwide.core.jpa.security.business.authority.util.CoreAuthorityConstants;

@Component
public class RepositoryAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryAuthenticationProvider.class);

	@Autowired
	private AuthenticationService authenticationService;

	@Override
	public UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) {
		String credentials = authentication != null ? (String) authentication.getCredentials() : null;
		if (logger.isDebugEnabled()) {
			logger.debug("Authenticating user: " + username + " with password: " + ((Strings.isNullOrEmpty(credentials)) ? "null" : "[PROTECTED]"));
		}
		try {
			RepositoryUser repositoryUser = authenticationService.authenticate(username, credentials);
			return buildUserDetails(repositoryUser, credentials);
		} catch (SecurityServiceException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Could not authenticate user: " + username, e);
			}
			throw new BadCredentialsException("Could not authenticate user: " + username, e);
		} catch (RepositoryRemoteException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Could not connect to repository while authenticating user: " + username, e);
			}
			throw new AuthenticationServiceException("Could not connect to repository while authenticating user: " + username, e);
		}
	}

	public UserDetails retrieveUser(String username) {
		if (logger.isDebugEnabled()) {
			logger.debug("Authenticating user: " + username);
		}
		try {
			RepositoryUser repositoryUser = authenticationService.authenticate(username);
			return buildUserDetails(repositoryUser, null);
		} catch (SecurityServiceException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Could not authenticate user: " + username, e);
			}
			throw new AuthenticationServiceException("Could not authenticate user: " + username, e);
		}
	}

	private UserDetails buildUserDetails(RepositoryUser repositoryUser, String credentials) {
		// Build authority list
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		// Add default Authenticated role
		authorities.add(new SimpleGrantedAuthority(CoreAuthorityConstants.ROLE_AUTHENTICATED));
		for (String group : repositoryUser.getGroups()) {
			authorities.add(new SimpleGrantedAuthority(group));
		}
		// Build user
		return new BusinessUser(repositoryUser, credentials, authorities);
	}

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) {
		// not needed
	}

}
