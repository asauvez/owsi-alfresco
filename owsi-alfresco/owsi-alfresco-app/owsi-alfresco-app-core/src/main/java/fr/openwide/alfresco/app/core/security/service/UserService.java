package fr.openwide.alfresco.app.core.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.base.Optional;

public interface UserService {

	Optional<Authentication> getCurrentAuthentication();

	Optional<UserDetails> getUserDetails(Authentication authentication);

	Optional<UserDetails> getCurrentUserDetails();

	Optional<String> getCurrentUsername();

}
