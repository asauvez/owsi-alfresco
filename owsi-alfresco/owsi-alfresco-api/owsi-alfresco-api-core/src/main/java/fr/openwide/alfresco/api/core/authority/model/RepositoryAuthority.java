package fr.openwide.alfresco.api.core.authority.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

public class RepositoryAuthority implements Serializable {

	private static final long serialVersionUID = -5556589719655849321L;

	public static final String GROUP_PREFIX = "GROUP_"; 
	
	public static final RepositoryAuthority USER_SYSTEM = RepositoryAuthority.authority("System");
	public static final RepositoryAuthority GROUP_EVERYONE = RepositoryAuthority.group("EVERYONE");
	
	public static final RepositoryAuthority GROUP_ALFRESCO_ADMINISTRATORS = RepositoryAuthority.group("ALFRESCO_ADMINISTRATORS");
	public static final RepositoryAuthority GROUP_ALFRESCO_MODEL_ADMINISTRATORS = RepositoryAuthority.group("ALFRESCO_MODEL_ADMINISTRATORS");
	public static final RepositoryAuthority GROUP_ALFRESCO_SEARCH_ADMINISTRATORS = RepositoryAuthority.group("ALFRESCO_SEARCH_ADMINISTRATORS");
	public static final RepositoryAuthority GROUP_EMAIL_CONTRIBUTORS = RepositoryAuthority.group("EMAIL_CONTRIBUTORS");
	public static final RepositoryAuthority GROUP_SITE_ADMINISTRATORS = RepositoryAuthority.group("SITE_ADMINISTRATORS");
	
	public static final RepositoryAuthority ROLE_ADMINISTRATOR = RepositoryAuthority.authority("ROLE_ADMINISTRATOR");

	private String name;

	private RepositoryAuthority(String name) {
		this.name = name;
	}
	
	public static RepositoryAuthority user(String username) {
		return authority(username);
	}
	public static RepositoryAuthority group(String shortName) {
		return authority(GROUP_PREFIX + shortName);
	}
	public static RepositoryAuthority authority(String authorityName) {
		return new RepositoryAuthority(authorityName);
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
