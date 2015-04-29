package fr.openwide.alfresco.app.core.security.service.impl;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.base.Optional;

import fr.openwide.alfresco.app.core.security.model.NamedUser;
import fr.openwide.alfresco.app.core.security.service.NamedUserService;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryUser;

public class NamedUserServiceImpl extends UserServiceImpl implements NamedUserService {

	@Override
	public NamedUser getUser(Authentication authentication) {
		Optional<UserDetails> userDetails = getUserDetails(authentication);
		return getUser(userDetails);
	}

	@Override
	public NamedUser getCurrentUser() {
		Optional<UserDetails> userDetails = getCurrentUserDetails();
		return getUser(userDetails);
	}

	public NamedUser getUser(Optional<UserDetails> userDetails) {
		if (! userDetails.isPresent()) {
			throw new IllegalStateException("Currently not in an authenticated context");
		} else if (userDetails.get() instanceof NamedUser) {
			return (NamedUser) userDetails.get();
		} else {
			throw new IllegalStateException("Currently held authentication is not a BusinessUser: " + userDetails.getClass());
		}
	}

	@Override
	public RepositoryTicket getTicket() {
		return getCurrentUser().getTicket();
	}

	@Override
	public RepositoryUser getTicketOwner() {
		return getCurrentUser().getRepositoryUser();
	}

}
