package fr.openwide.alfresco.app.core.security.service.impl;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.base.Optional;

import fr.openwide.alfresco.app.core.security.model.NamedUser;
import fr.openwide.alfresco.app.core.security.service.UserService;

public class UserServiceImpl implements UserService {

	@Override
	public Optional<Authentication> getCurrentAuthentication() {
		return Optional.fromNullable(SecurityContextHolder.getContext().getAuthentication());
	}

	@Override
	public Optional<UserDetails> getUserDetails(Authentication authentication) {
		if (authentication instanceof AnonymousAuthenticationToken || authentication == null) {
			// L'utilisateur n'est pas authentifié
			return Optional.absent();
		}
		Object principal = authentication.getPrincipal();
		if (principal instanceof UserDetails) {
			return Optional.of((UserDetails) principal);
		} else {
			throw new IllegalStateException("Currently held authentication is not a UserDetails: " + 
					((principal != null) ? principal.getClass() : null));
		}
	}

	@Override
	public Optional<UserDetails> getCurrentUserDetails() {
		Optional<Authentication> authentication = getCurrentAuthentication();
		return getUserDetails(authentication.orNull());
	}

	@Override
	public Optional<String> getCurrentUsername() {
		Optional<UserDetails> current = getCurrentUserDetails();
		return (current.isPresent()) ? Optional.of(current.get().getUsername()) : Optional.<String>absent();
	}

	@Override
	public boolean isAuthenticated() {
		return getCurrentAuthentication() != null;
	}
	
	@Override
	public NamedUser getAsNamedUser() {
		Optional<UserDetails> userDetails = getCurrentUserDetails();
		if (! userDetails.isPresent()) {
			throw new IllegalStateException("Currently not in an authenticated context. You may want to call Alfresco inside a runAsUser().");
		} else if (userDetails.get() instanceof NamedUser) {
			return (NamedUser) userDetails.get();
		} else {
			throw new IllegalStateException("Currently held authentication is not a NamedUser: " + userDetails.get().getClass() 
					+ ". You may want to call Alfresco inside a runAsUser().");
		}
	}

}
