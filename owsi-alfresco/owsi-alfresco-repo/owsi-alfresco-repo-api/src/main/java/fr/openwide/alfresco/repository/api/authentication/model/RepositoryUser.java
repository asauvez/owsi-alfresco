package fr.openwide.alfresco.repository.api.authentication.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RepositoryUser implements Serializable {

	private static final long serialVersionUID = 945185331538453155L;

	private UserReference userReference;

	private String firstName;
	private String lastName;
	private String email;

	private RepositoryTicket ticket;

	private boolean admin = false;
	private List<String> groups = new ArrayList<>();

	public UserReference getUserReference() {
		return userReference;
	}
	public void setUserReference(UserReference userReference) {
		this.userReference = userReference;
	}

	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public List<String> getGroups() {
		return groups;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public RepositoryTicket getTicket() {
		return ticket;
	}
	public void setTicket(RepositoryTicket ticket) {
		this.ticket = ticket;
	}

	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (object instanceof RepositoryUser) {
			RepositoryUser other = (RepositoryUser) object;
			return Objects.equals(userReference, other.getUserReference());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(userReference);

	}

}
