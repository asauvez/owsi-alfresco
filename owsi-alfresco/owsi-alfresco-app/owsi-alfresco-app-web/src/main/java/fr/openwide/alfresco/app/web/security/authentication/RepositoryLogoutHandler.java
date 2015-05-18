package fr.openwide.alfresco.app.web.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import fr.openwide.alfresco.app.core.security.service.RepositoryAuthenticationUserDetailsService;

public class RepositoryLogoutHandler implements LogoutHandler {

	private RepositoryAuthenticationUserDetailsService userDetailsService;

	public RepositoryLogoutHandler(RepositoryAuthenticationUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		if (authentication != null) {
			userDetailsService.logout(authentication);
		}
	}

}
