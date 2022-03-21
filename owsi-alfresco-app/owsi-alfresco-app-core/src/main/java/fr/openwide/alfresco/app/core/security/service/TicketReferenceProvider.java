package fr.openwide.alfresco.app.core.security.service;

import fr.openwide.alfresco.api.core.authentication.model.TicketReference;
import fr.openwide.alfresco.app.core.security.model.NamedUser;

public class TicketReferenceProvider {

	private UserService userService;
	private RepositoryAuthenticationUserDetailsService repositoryAuthenticationUserDetailsService;

	public TicketReferenceProvider(UserService userService, RepositoryAuthenticationUserDetailsService repositoryAuthenticationUserDetailsService) {
		this.userService = userService;
		this.repositoryAuthenticationUserDetailsService = repositoryAuthenticationUserDetailsService;
	}

	public TicketReference getTicket() {
		NamedUser namedUser = userService.getAsNamedUser();
		return namedUser.getRepositoryUser().getTicket();
	}

	public void renewTicket() {
		NamedUser namedUser = userService.getAsNamedUser();
		repositoryAuthenticationUserDetailsService.renewTicket(namedUser);
	}
}
