package fr.openwide.alfresco.app.core.security.service.impl;

import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.base.Optional;

import fr.openwide.alfresco.app.core.authentication.model.RepositoryUserProvider;
import fr.openwide.alfresco.app.core.security.service.RepositoryTicketProvider;
import fr.openwide.alfresco.app.core.security.service.UserService;
import fr.openwide.alfresco.api.core.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;

public class UserAwareRepositoryTicketProvider implements RepositoryTicketProvider {

	private UserService userService;

	public UserAwareRepositoryTicketProvider(UserService userService) {
		this.userService = userService;
	}

	@Override
	public RepositoryTicket getTicket() {
		RepositoryUserProvider provider = getUserProvider(userService.getCurrentUserDetails());
		return provider.getRepositoryUser().getTicket();
	}

	@Override
	public RepositoryUser getTicketOwner() {
		RepositoryUserProvider provider = getUserProvider(userService.getCurrentUserDetails());
		return provider.getRepositoryUser();
	}

	private RepositoryUserProvider getUserProvider(Optional<UserDetails> userDetails) {
		if (! userDetails.isPresent()) {
			throw new IllegalStateException("Currently not in an authenticated context");
		} else if (userDetails.orNull() instanceof RepositoryUserProvider) {
			return (RepositoryUserProvider) userDetails.orNull();
		} else {
			throw new IllegalStateException("Currently held authentication is not a RepositoryUserProvider: " + userDetails.getClass());
		}
	}

}
