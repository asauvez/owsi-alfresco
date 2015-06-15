package fr.openwide.alfresco.api.core.authentication.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class RepositoryUser implements Serializable {

	private static final long serialVersionUID = 945185331538453155L;

	public static final NameReference FIRST_NAME = NameReference.create("cm", "firstName"); 
	public static final NameReference LAST_NAME = NameReference.create("cm", "lastName"); 
	public static final NameReference EMAIL = NameReference.create("cm", "email"); 
	
	private UserReference userReference;

	private RepositoryNode userNode;
	private RepositoryTicket ticket;

	private boolean admin = false;
	private List<RepositoryAuthority> authorities = new ArrayList<>();

	public UserReference getUserReference() {
		return userReference;
	}
	public void setUserReference(UserReference userReference) {
		this.userReference = userReference;
	}

	public RepositoryNode getUserNode() {
		return userNode;
	}
	public void setUserNode(RepositoryNode userNode) {
		this.userNode = userNode;
	}

	public String getFirstName() {
		return userNode.getProperty(FIRST_NAME, String.class);
	}
	public String getLastName() {
		return userNode.getProperty(LAST_NAME, String.class);
	}
	public String getEmail() {
		return userNode.getProperty(EMAIL, String.class);
	}
	
	public List<RepositoryAuthority> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(List<RepositoryAuthority> authorities) {
		this.authorities = authorities;
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
		return Objects.hash(userReference);
	}

}
