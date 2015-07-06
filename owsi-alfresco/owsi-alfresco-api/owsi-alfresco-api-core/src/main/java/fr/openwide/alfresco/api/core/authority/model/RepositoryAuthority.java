package fr.openwide.alfresco.api.core.authority.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

public class RepositoryAuthority implements Serializable {

	private static final long serialVersionUID = -5556589719655849321L;

	public static final String GROUP_PREFIX = "GROUP_"; 
	
	public static final RepositoryAuthority GROUP_EVERYONE = new RepositoryAuthority(GROUP_PREFIX + "EVERYONE");
	public static final RepositoryAuthority GROUP_ADMINISTRATORS = new RepositoryAuthority(GROUP_PREFIX + "ALFRESCO_ADMINISTRATORS");

	private String name;

	public RepositoryAuthority(String name) {
		this.name = name;
	}
	
	public static RepositoryAuthority group(String shortName) {
		return new RepositoryAuthority(GROUP_PREFIX + shortName);
	}
	public String getGroupShortName() {
		if (! name.startsWith(GROUP_PREFIX)) {
			throw new IllegalStateException(name);
		}
		return name.substring(GROUP_PREFIX.length());
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
