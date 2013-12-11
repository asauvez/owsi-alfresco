package fr.openwide.alfresco.app.core.security.service.impl;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import fr.openwide.alfresco.app.core.security.model.BusinessUser;
import fr.openwide.alfresco.app.core.security.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Override
	public BusinessUser getUser(Authentication authentication) {
		if (authentication == null) {
			throw new IllegalStateException("Currently not in an authentified thread");
		}
		if (authentication instanceof AnonymousAuthenticationToken) {
			// L'utilisateur n'est pas authentifié
			return null;
		}
		Object principal = authentication.getPrincipal();
		if (principal instanceof BusinessUser) {
			return (BusinessUser) principal;
		} else {
			throw new IllegalStateException("Currently held authentication is not a BusinessUser: " + principal.getClass());
		}
	}

	@Override
	public BusinessUser getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return getUser(authentication);
	}

	@Override
	public String getCurrentUserId() {
		return getCurrentUser().getUsername();
	}

}
