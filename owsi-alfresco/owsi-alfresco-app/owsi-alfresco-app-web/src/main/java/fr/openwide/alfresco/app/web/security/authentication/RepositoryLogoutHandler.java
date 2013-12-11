package fr.openwide.alfresco.app.web.security.authentication;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import fr.openwide.alfresco.app.core.authentication.service.AuthenticationService;
import fr.openwide.alfresco.app.core.security.model.BusinessUser;
import fr.openwide.alfresco.app.core.security.service.UserService;
import fr.openwide.alfresco.repository.api.remote.model.AccessDeniedRemoteException;

@Component
public class RepositoryLogoutHandler implements LogoutHandler {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryLogoutHandler.class);

	@Autowired
	private UserService userService;
	@Autowired
	private AuthenticationService authenticationService;

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		if (authentication != null) {
			BusinessUser user = userService.getUser(authentication);
			try {
				authenticationService.logout(user.getTicket());
			} catch (AccessDeniedRemoteException e) {
				logger.warn("Could not logout user: " + user.getUsername(), e);
			}
		}
	}

}
