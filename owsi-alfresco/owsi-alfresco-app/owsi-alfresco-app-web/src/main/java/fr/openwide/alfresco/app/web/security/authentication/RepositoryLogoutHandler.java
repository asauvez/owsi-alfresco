package fr.openwide.alfresco.app.web.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.session.HttpSessionDestroyedEvent;

import fr.openwide.alfresco.api.core.remote.exception.AccessDeniedRemoteException;
import fr.openwide.alfresco.app.core.security.service.RepositoryAuthenticationUserDetailsService;

/**
 * Pour pouvoir avoir l'évenement de session timeout, il est nécessaire de rajouter ces lignes dans web.xml.
 * 
 * <listener>
 *  	<listener-class>org.springframework.security.web.session.HttpSessionEventPublisher</listener-class>
 * </listener>
*/
public class RepositoryLogoutHandler implements LogoutHandler, ApplicationListener<HttpSessionDestroyedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryLogoutHandler.class);

	private RepositoryAuthenticationUserDetailsService userDetailsService;

	public RepositoryLogoutHandler(RepositoryAuthenticationUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	/**
	 * Appeler sur un logout explicite.
	 */
	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		logoutRepository(authentication);
	}

	/**
	 * Appeler sur un session timeout. Logout le ticket Alfresco.
	 */
	@Override
	public void onApplicationEvent(HttpSessionDestroyedEvent event) {
		for (SecurityContext context : event.getSecurityContexts()) {
			Authentication authentication = context.getAuthentication();
			logoutRepository(authentication);
		}
	}
	
	private void logoutRepository(Authentication authentication) {
		if (authentication != null) {
			try {
				userDetailsService.logout(authentication);
			} catch (AccessDeniedRemoteException e) {
				LOGGER.warn("Could not logout authentication on the repository: {}", authentication);
			}
		}
	}
}
