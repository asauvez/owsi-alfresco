package fr.openwide.alfresco.repository.api.authentication.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

public class UserReference implements Serializable {

	private static final long serialVersionUID = -3903180477207296871L;

	private final String username;

	public UserReference(String username) {
		this.username = username;
	}

	@JsonValue
	public String getUsername() {
		return username;
	}

	@Override
	public String toString() {
		return username;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (object instanceof UserReference) {
			UserReference other = (UserReference) object;
			return Objects.equals(username, other.getUsername());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(username);
	}

}
