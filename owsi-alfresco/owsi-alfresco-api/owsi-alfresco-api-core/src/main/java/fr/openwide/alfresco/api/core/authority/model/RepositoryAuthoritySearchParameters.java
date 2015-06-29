package fr.openwide.alfresco.api.core.authority.model;

import java.io.Serializable;

import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class RepositoryAuthoritySearchParameters implements Serializable {
	
	private RepositoryAuthority parentAuthority;

	private NameReference filterProperty;
	private String filterValue;
	
	private boolean immediate;
	private NodeScope nodeScope;

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
	
	public NodeScope getNodeScope() {
		return nodeScope;
	}
	public void setNodeScope(NodeScope nodeScope) {
		this.nodeScope = nodeScope;
	}
}
