package fr.openwide.alfresco.api.core.authentication.model;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.common.base.Strings;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class RepositoryUser implements Serializable {

	private UserReference userReference;

	private RepositoryNode userNode;
	private TicketReference ticket;

	private boolean admin = false;
	private List<AuthorityReference> authorities = new ArrayList<>();

	public UserReference getUserReference() {
		return userReference;
	}
	public void setUserReference(UserReference userReference) {
		this.userReference = userReference;
	}

	public RepositoryNode getUserNode() {
		return userNode;
	}
	public BusinessNode getUserBusinessNode() {
		return new BusinessNode(userNode);
	}
	public void setUserNode(RepositoryNode userNode) {
		this.userNode = userNode;
	}

	public String getFirstName() {
		return userNode.getProperty(CmModel.person.firstName.getNameReference(), String.class);
	}
	public String getLastName() {
		return userNode.getProperty(CmModel.person.lastName.getNameReference(), String.class);
	}
	public String getFullName() {
		String firstName = getFirstName();
		String lastName = getLastName();
		return ((firstName != null) ? firstName : "") + " " + ((lastName != null) ? lastName : "").trim();
	}
	public String getEmail() {
		return userNode.getProperty(CmModel.person.email.getNameReference(), String.class);
	}
	
	public List<AuthorityReference> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(List<AuthorityReference> authorities) {
		this.authorities = authorities;
	}

	public TicketReference getTicket() {
		return ticket;
	}
	public void setTicket(TicketReference ticket) {
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

	/** {@see org.springframework.security.core.userdetails.User#toString()} */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("UserReference: ").append(userReference).append("; ");
		if (ticket != null) {
			sb.append("Ticket: ").append(Strings.isNullOrEmpty(ticket.getTicket()) ? ticket : "[PROTECTED]").append("; ");
		}
		sb.append("Admin: ").append(admin).append("; ");
		if (authorities != null && ! authorities.isEmpty()) {
			sb.append("Repository Authorities: ");
			boolean first = true;
			for (AuthorityReference auth : authorities) {
				if (! first) {
					sb.append(",");
				}
				first = false;
				sb.append(auth);
			}
		} else {
			sb.append("Not granted any authorities in the repository");
		}
		return sb.toString();
	}

}
