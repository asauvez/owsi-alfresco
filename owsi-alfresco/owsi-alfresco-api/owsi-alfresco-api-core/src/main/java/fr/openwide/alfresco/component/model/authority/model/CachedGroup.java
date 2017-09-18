package fr.openwide.alfresco.component.model.authority.model;


import java.io.Serializable;
import java.util.Objects;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;

public class CachedGroup implements Serializable {

	private AuthorityReference authorityReference;
	private String displayName;

	public CachedGroup(AuthorityReference authorityReference, String displayName) {
		this.authorityReference = authorityReference;
		this.displayName = displayName;
	}

	public AuthorityReference getAuthorityReference() {
		return authorityReference;
	}
	public String getFullName() {
		return authorityReference.getName();
	}
	public String getShortName() {
		return authorityReference.getGroupShortName();
	}
	
	public String getDisplayName() {
		return displayName;
	}

	
	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (object instanceof CachedGroup) {
			CachedGroup other = (CachedGroup) object;
			return Objects.equals(authorityReference, other.getAuthorityReference());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(authorityReference);
	}

	@Override
	public String toString() {
		return authorityReference.toString();
	}

}
