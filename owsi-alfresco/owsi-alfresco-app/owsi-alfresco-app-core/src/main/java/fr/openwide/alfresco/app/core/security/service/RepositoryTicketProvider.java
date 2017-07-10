package fr.openwide.alfresco.app.core.security.service;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.app.core.security.model.NamedUser;

public class RepositoryTicketProvider {

	private UserService userService;
	private RepositoryAuthenticationUserDetailsService repositoryAuthenticationUserDetailsService;

	public RepositoryTicketProvider(UserService userService, RepositoryAuthenticationUserDetailsService repositoryAuthenticationUserDetailsService) {
		this.userService = userService;
		this.repositoryAuthenticationUserDetailsService = repositoryAuthenticationUserDetailsService;
	}

	public RepositoryTicket getTicket() {
		NamedUser namedUser = userService.getAsNamedUser();
		return namedUser.getRepositoryUser().getTicket();
	}

	public void renewTicket() {
		NamedUser namedUser = userService.getAsNamedUser();
		repositoryAuthenticationUserDetailsService.renewTicket(namedUser);
	}
}
