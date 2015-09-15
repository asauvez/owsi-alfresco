package fr.openwide.alfresco.api.core.authority.model;

import fr.openwide.alfresco.api.core.node.model.AbstractQueryParameters;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class RepositoryAuthorityQueryParameters extends AbstractQueryParameters {
	
	private RepositoryAuthority parentAuthority;

	private NameReference filterProperty;
	private String filterValue;
	
	private boolean immediate;

	public RepositoryAuthority getParentAuthority() {
		return parentAuthority;
	}
	public void setParentAuthority(RepositoryAuthority parentAuthority) {
		this.parentAuthority = parentAuthority;
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
	
}
