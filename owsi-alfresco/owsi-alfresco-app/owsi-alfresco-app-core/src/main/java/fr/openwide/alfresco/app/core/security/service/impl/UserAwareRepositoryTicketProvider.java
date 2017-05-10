package fr.openwide.alfresco.app.core.security.service.impl;

import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.app.core.authentication.model.RepositoryUserProvider;
import fr.openwide.alfresco.app.core.security.service.RepositoryAuthenticationUserDetailsService;
import fr.openwide.alfresco.app.core.security.service.RepositoryTicketProvider;
import fr.openwide.alfresco.app.core.security.service.UserService;

public class UserAwareRepositoryTicketProvider implements RepositoryTicketProvider {

	private UserService userService;
	private RepositoryAuthenticationUserDetailsService repositoryAuthenticationUserDetailsService;

	public UserAwareRepositoryTicketProvider(UserService userService, RepositoryAuthenticationUserDetailsService repositoryAuthenticationUserDetailsService) {
		this.userService = userService;
		this.repositoryAuthenticationUserDetailsService = repositoryAuthenticationUserDetailsService;
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
		} else if (userDetails.get() instanceof RepositoryUserProvider) {
			return (RepositoryUserProvider) userDetails.get();
		} else {
			throw new IllegalStateException("Currently held authentication is not a RepositoryUserProvider: " + userDetails.get().getClass());
		}
	}

	@Override
	public void renewTicket() {
		repositoryAuthenticationUserDetailsService.renewTicket(getTicketOwner());
	}
}
