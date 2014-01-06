package fr.openwide.alfresco.repository.api.node.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

public class RepositoryAuthority implements Serializable {

	private static final long serialVersionUID = -5556589719655849321L;

	public static final RepositoryAuthority GROUP_EVERYONE = new RepositoryAuthority("GROUP_EVERYONE");
	public static final RepositoryAuthority GROUP_ADMINISTRATORS = new RepositoryAuthority("GROUP_ALFRESCO_ADMINISTRATORS");

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
			return Objects.equals(name, other.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

}
