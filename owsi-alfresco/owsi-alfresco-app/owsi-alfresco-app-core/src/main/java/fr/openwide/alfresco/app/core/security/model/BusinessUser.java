package fr.openwide.alfresco.app.core.security.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import fr.openwide.alfresco.app.core.authentication.model.RepositoryTicketAware;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryUser;

public class BusinessUser extends User implements RepositoryTicketAware {

	private static final long serialVersionUID = 336652943037329710L;

	private static final String GROUP_PREFIX = "GROUP_";

	private RepositoryUser repositoryUser;

	public BusinessUser(RepositoryUser repositoryUser, String password, Collection<? extends GrantedAuthority> authorities) {
		super(repositoryUser.getUserReference().getUsername(), password != null ? password : "N/A", authorities);
		this.repositoryUser = repositoryUser;
	}

	public String getFirstName() {
		return repositoryUser.getFirstName();
	}

	public String getPrenom() {
		return repositoryUser.getLastName();
	}

	public String getEmail() {
		return repositoryUser.getEmail();
	}

	public boolean isAdmin() {
		return repositoryUser.isAdmin();
	}

	@Override
	public RepositoryTicket getTicket() {
		return repositoryUser.getTicket();
	}

	public RepositoryUser getRepositoryUser() {
		return repositoryUser;
	}

	public boolean hasRole(String role) {
		String prefixedRole = role;
		if (! prefixedRole.startsWith(GROUP_PREFIX)) {
			prefixedRole = GROUP_PREFIX + prefixedRole;
		}
		for (GrantedAuthority auth : getAuthorities()) {
			if (auth.getAuthority().equals(prefixedRole)) {
				return true;
			}
		}
		return false;
	}

}
