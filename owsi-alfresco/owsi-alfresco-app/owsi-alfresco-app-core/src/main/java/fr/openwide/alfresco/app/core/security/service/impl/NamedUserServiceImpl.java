package fr.openwide.alfresco.app.core.security.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.base.Optional;

import fr.openwide.alfresco.app.core.security.model.NamedUser;
import fr.openwide.alfresco.app.core.security.service.NamedUserService;

public class NamedUserServiceImpl extends UserServiceImpl implements NamedUserService {

	@Override
	public NamedUser getUser(Authentication authentication) {
		Optional<UserDetails> userDetails = getUserDetails(authentication);
		return getNamedUser(userDetails);
	}

	@Override
	public NamedUser getCurrentUser() {
		Optional<UserDetails> userDetails = getCurrentUserDetails();
		return getNamedUser(userDetails);
	}

	private NamedUser getNamedUser(Optional<UserDetails> userDetails) {
		if (! userDetails.isPresent()) {
			throw new IllegalStateException("Currently not in an authenticated context");
		} else if (userDetails.orNull() instanceof NamedUser) {
			return (NamedUser) userDetails.orNull();
		} else {
			throw new IllegalStateException("Currently held authentication is not a BusinessUser: " + userDetails.getClass());
		}
	}

}
