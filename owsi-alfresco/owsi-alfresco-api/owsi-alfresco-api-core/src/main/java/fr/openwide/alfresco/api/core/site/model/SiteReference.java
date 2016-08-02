package fr.openwide.alfresco.api.core.site.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;

public class SiteReference {
	
	public static final String DOCUMENTLIBRARY = "cm:documentlibrary";
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

	public RepositoryAuthority getGroupeCollaborator() {
		return RepositoryAuthority.group("site_" + getName() + "_SiteCollaborator");
	}
	public RepositoryAuthority getGroupeConsumer() {
		return RepositoryAuthority.group("site_" + getName() + "_SiteConsumer");
	}
	public RepositoryAuthority getGroupeContributor() {
		return RepositoryAuthority.group("site_" + getName() + "_SiteContributor");
	}
	public RepositoryAuthority getGroupeManager() {
		return RepositoryAuthority.group("site_" + getName() + "_SiteManager");
	}

	public String getPath() {
		return "/app:company_home/st:sites/cm:" + getName();
	}
	public String getPathDocumentLibrary() {
		return getPath() + "/" + DOCUMENTLIBRARY;
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
