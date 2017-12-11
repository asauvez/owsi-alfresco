package fr.openwide.alfresco.app.core.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import fr.openwide.alfresco.app.core.security.model.NamedUser;

public interface UserService {

	Optional<Authentication> getCurrentAuthentication();

	Optional<UserDetails> getUserDetails(Authentication authentication);

	Optional<UserDetails> getCurrentUserDetails();

	Optional<String> getCurrentUsername();

	boolean isAuthenticated();
	NamedUser getAsNamedUser();
}
