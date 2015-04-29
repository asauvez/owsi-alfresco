package fr.openwide.alfresco.app.core.security.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import fr.openwide.alfresco.app.core.authentication.model.RepositoryTicketProvider;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryUser;

public class NamedUser extends User implements RepositoryTicketProvider {

	private static final long serialVersionUID = 336652943037329710L;

	private RepositoryUser repositoryUser;

	public NamedUser(RepositoryUser repositoryUser, String password, Collection<? extends GrantedAuthority> authorities) {
		super(repositoryUser.getUserReference().getUsername(), password != null ? password : "N/A", authorities);
		this.repositoryUser = repositoryUser;
	}

	public String getFirstName() {
		return repositoryUser.getFirstName();
	}

	public String getLastName() {
		return repositoryUser.getLastName();
	}

	public String getEmail() {
		return repositoryUser.getEmail();
	}

	@Override
	public RepositoryTicket getTicket() {
		return repositoryUser.getTicket();
	}

	@Override
	public RepositoryUser getTicketOwner() {
		return repositoryUser;
	}

	public RepositoryUser getRepositoryUser() {
		return repositoryUser;
	}

}
