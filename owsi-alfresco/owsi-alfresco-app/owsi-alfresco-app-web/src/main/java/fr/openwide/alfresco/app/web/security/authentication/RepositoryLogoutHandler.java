package fr.openwide.alfresco.app.web.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import fr.openwide.alfresco.api.core.remote.exception.AccessDeniedRemoteException;
import fr.openwide.alfresco.app.core.security.service.RepositoryAuthenticationUserDetailsService;

public class RepositoryLogoutHandler implements LogoutHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryLogoutHandler.class);

	private RepositoryAuthenticationUserDetailsService userDetailsService;

	public RepositoryLogoutHandler(RepositoryAuthenticationUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		if (authentication != null) {
			try {
				userDetailsService.logout(authentication);
			} catch (AccessDeniedRemoteException e) {
				LOGGER.warn("Could not logout authentication on the repository: {}", authentication);
			}
		}
	}

}
