package fr.openwide.alfresco.api.core.authority.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

public class AuthorityReference implements Serializable {

	private static final long serialVersionUID = -5556589719655849321L;

	public static final String GROUP_PREFIX = "GROUP_";
	public static final String ROLE_PREFIX = "ROLE_"; 
	
	public static final AuthorityReference USER_SYSTEM = AuthorityReference.authority("System");
	public static final AuthorityReference GROUP_EVERYONE = AuthorityReference.group("EVERYONE");
	
	public static final AuthorityReference GROUP_ALFRESCO_ADMINISTRATORS = AuthorityReference.group("ALFRESCO_ADMINISTRATORS");
	public static final AuthorityReference GROUP_ALFRESCO_MODEL_ADMINISTRATORS = AuthorityReference.group("ALFRESCO_MODEL_ADMINISTRATORS");
	public static final AuthorityReference GROUP_ALFRESCO_SEARCH_ADMINISTRATORS = AuthorityReference.group("ALFRESCO_SEARCH_ADMINISTRATORS");
	public static final AuthorityReference GROUP_EMAIL_CONTRIBUTORS = AuthorityReference.group("EMAIL_CONTRIBUTORS");
	public static final AuthorityReference GROUP_SITE_ADMINISTRATORS = AuthorityReference.group("SITE_ADMINISTRATORS");
	
	public static final AuthorityReference ROLE_ADMINISTRATOR = AuthorityReference.authority("ROLE_ADMINISTRATOR");
	public static final AuthorityReference ROLE_OWNER = AuthorityReference.authority("ROLE_OWNER");
	public static final AuthorityReference ROLE_LOCK_OWNER = AuthorityReference.authority("ROLE_LOCK_OWNER");
	public static final AuthorityReference ROLE_GUEST = AuthorityReference.authority("ROLE_GUEST");

	private String name;

	private AuthorityReference(String name) {
		this.name = name;
	}
	
	public static AuthorityReference user(String username) {
		return authority(username);
	}
	public static AuthorityReference group(String shortName) {
		return authority(GROUP_PREFIX + shortName);
	}
	public static AuthorityReference role(String shortName) {
		return authority(ROLE_PREFIX + shortName);
	}
	public static AuthorityReference authority(String authorityName) {
		return new AuthorityReference(authorityName);
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
		if (object instanceof AuthorityReference) {
			AuthorityReference other = (AuthorityReference) object;
			return Objects.equals(name, other.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

}
