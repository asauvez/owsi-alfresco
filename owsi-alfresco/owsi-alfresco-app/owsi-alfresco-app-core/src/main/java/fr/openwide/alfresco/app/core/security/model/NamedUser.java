package fr.openwide.alfresco.app.core.security.model;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.app.core.authentication.model.RepositoryUserProvider;

public class NamedUser extends User implements RepositoryUserProvider {

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
	public RepositoryUser getRepositoryUser() {
		return repositoryUser;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append("; ");
		sb.append("RepositoryUser: ").append(repositoryUser);
		return sb.toString();
	}

}
