package fr.openwide.alfresco.app.core.security.service;

import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.app.core.security.model.NamedUser;

public class RepositoryTicketProvider {

	private UserService userService;
	private RepositoryAuthenticationUserDetailsService repositoryAuthenticationUserDetailsService;

	public RepositoryTicketProvider(UserService userService, RepositoryAuthenticationUserDetailsService repositoryAuthenticationUserDetailsService) {
		this.userService = userService;
		this.repositoryAuthenticationUserDetailsService = repositoryAuthenticationUserDetailsService;
	}

	public RepositoryTicket getTicket() {
		NamedUser user = getNamedUser(userService.getCurrentUserDetails());
		return user.getRepositoryUser().getTicket();
	}

	public RepositoryUser getTicketOwner() {
		NamedUser user = getNamedUser(userService.getCurrentUserDetails());
		return user.getRepositoryUser();
	}

	private NamedUser getNamedUser(Optional<UserDetails> userDetails) {
		if (! userDetails.isPresent()) {
			throw new IllegalStateException("Currently not in an authenticated context. You may want to call Alfresco inside a runAsUser().");
		} else if (userDetails.get() instanceof NamedUser) {
			return (NamedUser) userDetails.get();
		} else {
			throw new IllegalStateException("Currently held authentication is not a NamedUser: " + userDetails.get().getClass() 
					+ ". You may want to call Alfresco inside a runAsUser().");
		}
	}

	public void renewTicket() {
		NamedUser user = getNamedUser(userService.getCurrentUserDetails());
		repositoryAuthenticationUserDetailsService.renewTicket(user);
	}
}
