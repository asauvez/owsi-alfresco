package fr.openwide.alfresco.app.core.security.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.hierarchicalroles.NullRoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.google.common.base.Strings;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.remote.exception.AccessDeniedRemoteException;
import fr.openwide.alfresco.api.core.remote.exception.UnauthorizedRemoteException;
import fr.openwide.alfresco.app.core.authentication.service.AuthenticationService;
import fr.openwide.alfresco.app.core.remote.model.RepositoryConnectException;
import fr.openwide.alfresco.app.core.security.model.NamedUser;
import fr.openwide.alfresco.app.core.security.service.RepositoryAuthenticationUserDetailsService;
import fr.openwide.core.jpa.security.business.authority.util.CoreAuthorityConstants;

public class RepositoryAuthenticationUserDetailsServiceImpl implements RepositoryAuthenticationUserDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryAuthenticationUserDetailsServiceImpl.class);

	private AuthenticationService authenticationService;
	private RoleHierarchy loginTimeRoleHierarchy;

	public RepositoryAuthenticationUserDetailsServiceImpl(AuthenticationService authenticationService) {
		this(authenticationService, null);
	}
	
	public RepositoryAuthenticationUserDetailsServiceImpl(AuthenticationService authenticationService, RoleHierarchy loginTimeRoleHierarchy) {
		this.authenticationService = authenticationService;
		this.loginTimeRoleHierarchy = (loginTimeRoleHierarchy != null) ? loginTimeRoleHierarchy : new NullRoleHierarchy();
	}

	@Override
	public NamedUser loadUserDetails(UsernamePasswordAuthenticationToken token) throws UsernameNotFoundException {
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
	public NamedUser loadUserByUsername(String username) throws UsernameNotFoundException {
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

	private NamedUser buildUserDetails(RepositoryUser repositoryUser, String credentials) {
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
		return new NamedUser(repositoryUser, credentials, 
				loginTimeRoleHierarchy.getReachableGrantedAuthorities(authorities));
	}

	@Override
	public void logout(NamedUser user) {
		authenticationService.logout(user.getRepositoryUser().getTicket());
	}

	@Override
	public void renewTicket(NamedUser user) {
		RepositoryUser repositoryUser = user.getRepositoryUser();
		try {
			authenticationService.logout(repositoryUser.getTicket());
		} catch (UnauthorizedRemoteException ex) {
			// nop
		}
		
		String userName = repositoryUser.getUserReference().getUsername();
		NamedUser userDetails = loadUserByUsername(userName);
		repositoryUser.setTicket(userDetails.getRepositoryUser().getTicket());
	}

}
