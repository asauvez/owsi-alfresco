package fr.openwide.alfresco.repository.api.authentication.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

public class RepositoryTicket implements Serializable {

	private static final long serialVersionUID = 6119928419258252489L;

	private final String ticket;

	public RepositoryTicket(String ticket) {
		this.ticket = ticket;
	}

	@JsonValue
	public String getTicket() {
		return ticket;
	}

	@Override
	public String toString() {
		return getTicket();
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (object instanceof RepositoryTicket) {
			RepositoryTicket other = (RepositoryTicket) object;
			return Objects.equals(ticket, other.getTicket());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(ticket);

	}

}
