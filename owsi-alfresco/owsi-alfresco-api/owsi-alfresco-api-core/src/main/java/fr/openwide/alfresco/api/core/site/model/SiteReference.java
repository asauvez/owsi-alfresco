package fr.openwide.alfresco.api.core.site.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;

public class SiteReference {
	
	public static final String PATH_DOCUMENTLIBRARY = "cm:documentlibrary";
	public static final String WIKI = "cm:wiki";
	
	private String name;

	private SiteReference(String name) {
		this.name = name;
	}
	public static SiteReference create(String name) {
		return new SiteReference(name);
	}

	@JsonValue
	public String getName() {
		return name;
	}

	public AuthorityReference getGroupeCollaborator() {
		return AuthorityReference.group("site_" + getName() + "_SiteCollaborator");
	}
	public AuthorityReference getGroupeConsumer() {
		return AuthorityReference.group("site_" + getName() + "_SiteConsumer");
	}
	public AuthorityReference getGroupeContributor() {
		return AuthorityReference.group("site_" + getName() + "_SiteContributor");
	}
	public AuthorityReference getGroupeManager() {
		return AuthorityReference.group("site_" + getName() + "_SiteManager");
	}

	public String getPath() {
		return "/app:company_home/st:sites/cm:" + getName();
	}
	public String getPathDocumentLibrary() {
		return getPath() + "/" + PATH_DOCUMENTLIBRARY;
	}
	public String getPathWiki() {
		return getPath() + "/" + WIKI;
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
		if (object instanceof SiteReference) {
			SiteReference other = (SiteReference) object;
			return Objects.equals(name, other.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

}
