package fr.openwide.alfresco.repository.api.node.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

public class RepositoryAuthority implements Serializable {
	
	public static final RepositoryAuthority ROLE_GUEST = new RepositoryAuthority("ROLE_GUEST");
	public static final RepositoryAuthority ROLE_EVERYONE = new RepositoryAuthority("GROUP_EVERYONE");
	public static final RepositoryAuthority ROLE_ADMINISTRATOR = new RepositoryAuthority("ROLE_ADMINISTRATOR");

	public static final RepositoryAuthority ROLE_OWNER = new RepositoryAuthority("ROLE_OWNER");
	public static final RepositoryAuthority ROLE_LOCK_OWNER = new RepositoryAuthority("ROLE_LOCK_OWNER");

	private String name;
	
	public RepositoryAuthority(String name) {
		this.name = name;
	}

	@JsonValue
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (object instanceof RepositoryAuthority) {
			RepositoryAuthority other = (RepositoryAuthority) object;
			return Objects.equals(getName(), other.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getName());
	}}
