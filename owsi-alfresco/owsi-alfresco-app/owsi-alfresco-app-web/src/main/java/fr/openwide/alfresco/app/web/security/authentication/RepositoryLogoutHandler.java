package fr.openwide.alfresco.app.web.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import fr.openwide.alfresco.app.core.authentication.model.RepositoryTicketProvider;
import fr.openwide.alfresco.app.core.authentication.service.AuthenticationService;
import fr.openwide.alfresco.app.core.security.service.NamedUserService;

@Component
public class RepositoryLogoutHandler implements LogoutHandler {

	@Autowired
	private NamedUserService userService;
	@Autowired
	private AuthenticationService authenticationService;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		if (authentication != null) {
			RepositoryTicketProvider ticketProvider = userService.getUser(authentication);
			authenticationService.logout(ticketProvider.getTicket());
		}
	}

}
