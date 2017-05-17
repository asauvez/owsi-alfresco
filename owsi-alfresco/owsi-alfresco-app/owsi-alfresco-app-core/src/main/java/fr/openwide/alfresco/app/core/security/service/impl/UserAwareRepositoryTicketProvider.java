package fr.openwide.alfresco.app.core.security.service.impl;

import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.app.core.security.model.NamedUser;
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
		NamedUser user = getNamedUser(userService.getCurrentUserDetails());
		return user.getRepositoryUser().getTicket();
	}

	@Override
	public RepositoryUser getTicketOwner() {
		NamedUser user = getNamedUser(userService.getCurrentUserDetails());
		return user.getRepositoryUser();
	}

	private NamedUser getNamedUser(Optional<UserDetails> userDetails) {
		if (! userDetails.isPresent()) {
			throw new IllegalStateException("Currently not in an authenticated context");
		} else if (userDetails.get() instanceof NamedUser) {
			return (NamedUser) userDetails.get();
		} else {
			throw new IllegalStateException("Currently held authentication is not a NamedUser: " + userDetails.get().getClass());
		}
	}

	@Override
	public void renewTicket() {
		repositoryAuthenticationUserDetailsService.renewTicket(getTicketOwner());
	}
}
