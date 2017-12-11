package fr.openwide.alfresco.api.core.authority.model;

import fr.openwide.alfresco.api.core.node.model.AbstractQueryParameters;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class AuthorityQueryParameters extends AbstractQueryParameters {
	
	private AuthorityReference parentAuthority = AuthorityReference.GROUP_EVERYONE;
	private AuthorityTypeReference authorityType;
	private ZoneReference zone;

	private NameReference filterProperty;
	private String filterValue;
	
	private boolean immediate;
	private boolean includingParent;

	public AuthorityReference getParentAuthority() {
		return parentAuthority;
	}
	public void setParentAuthority(AuthorityReference parentAuthority) {
		this.parentAuthority = parentAuthority;
	}
	public AuthorityTypeReference getAuthorityType() {
		return authorityType;
	}
	public void setAuthorityType(AuthorityTypeReference authorityType) {
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
