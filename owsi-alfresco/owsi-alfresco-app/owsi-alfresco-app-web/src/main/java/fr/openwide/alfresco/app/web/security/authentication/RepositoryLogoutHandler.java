package fr.openwide.alfresco.app.web.security.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
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
public class RepositoryLogoutHandler implements ApplicationListener<HttpSessionDestroyedEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryLogoutHandler.class);

	private RepositoryAuthenticationUserDetailsService userDetailsService;

	public RepositoryLogoutHandler(RepositoryAuthenticationUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	public void onApplicationEvent(HttpSessionDestroyedEvent event) {
		for (SecurityContext context : event.getSecurityContexts()) {
			Authentication authentication = context.getAuthentication();
			if (authentication != null) {
				try {
					userDetailsService.logout(authentication);
				} catch (AccessDeniedRemoteException e) {
					LOGGER.warn("Could not logout authentication on the repository: {}", authentication);
				}
			}
		}
	}
}
