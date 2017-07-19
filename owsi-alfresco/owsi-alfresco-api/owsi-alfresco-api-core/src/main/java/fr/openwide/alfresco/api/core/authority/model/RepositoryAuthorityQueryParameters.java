package fr.openwide.alfresco.api.core.authority.model;

import fr.openwide.alfresco.api.core.node.model.AbstractQueryParameters;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class RepositoryAuthorityQueryParameters extends AbstractQueryParameters {
	
	private RepositoryAuthority parentAuthority = RepositoryAuthority.GROUP_EVERYONE;
	private RepositoryAuthorityType authorityType;
	private ZoneReference zone;

	private NameReference filterProperty;
	private String filterValue;
	
	private boolean immediate;
	private boolean includingParent;

	public RepositoryAuthority getParentAuthority() {
		return parentAuthority;
	}
	public void setParentAuthority(RepositoryAuthority parentAuthority) {
		this.parentAuthority = parentAuthority;
	}
	public RepositoryAuthorityType getAuthorityType() {
		return authorityType;
	}
	public void setAuthorityType(RepositoryAuthorityType authorityType) {
		this.authorityType = authorityType;
	}
	public ZoneReference getZone() {
		return zone;
	}
	public void setZone(ZoneReference zone) {
		this.zone = zone;
	}
	public NameReference getFilterProperty() {
		return filterProperty;
	}
	public void setFilterProperty(NameReference filterProperty) {
		this.filterProperty = filterProperty;
	}
	public String getFilterValue() {
		return filterValue;
	}
	public void setFilterValue(String filterValue) {
		this.filterValue = filterValue;
	}
	
	public boolean isImmediate() {
		return immediate;
	}
	public void setImmediate(boolean immediate) {
		this.immediate = immediate;
	}
	
	public boolean isIncludingParent() {
		return includingParent;
	}
	public void setIncludingParent(boolean includingParent) {
		this.includingParent = includingParent;
	}
	
}
