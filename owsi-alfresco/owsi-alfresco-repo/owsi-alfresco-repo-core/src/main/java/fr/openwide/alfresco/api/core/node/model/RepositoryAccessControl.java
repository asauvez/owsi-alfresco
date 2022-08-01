package fr.openwide.alfresco.api.core.node.model;

import java.io.Serializable;
import java.util.Objects;

import org.alfresco.service.cmr.repository.NodeRef;

import com.google.common.base.Joiner;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;

public class RepositoryAccessControl implements Serializable {

	private NodeRef nodeRef;
	private AuthorityReference authority;
	private PermissionReference permission;
	private boolean allowed;
	private boolean inherited;

	public RepositoryAccessControl() {}

	public RepositoryAccessControl(NodeRef nodeRef, AuthorityReference authority, PermissionReference permission, boolean allowed, boolean inherited) {
		this.nodeRef = nodeRef;
		this.authority = authority;
		this.permission = permission;
		this.allowed = allowed;
		this.inherited = inherited;
	}

	public NodeRef getNodeRef() {
		return nodeRef;
	}
	public void setNodeRef(NodeRef nodeRef) {
		this.nodeRef = nodeRef;
	}

	public AuthorityReference getAuthority() {
		return authority;
	}
	public void setAuthority(AuthorityReference authority) {
		this.authority = authority;
	}

	public PermissionReference getPermission() {
		return permission;
	}
	public void setPermission(PermissionReference permission) {
		this.permission = permission;
	}

	public boolean isAllowed() {
		return allowed;
	}
	public void setAllowed(boolean allowed) {
		this.allowed = allowed;
	}

	public boolean isInherited() {
		return inherited;
	}
	public void setInherited(boolean inherited) {
		this.inherited = inherited;
	}
	
	@Override
	public String toString() {
		return Joiner.on(":").join(nodeRef, authority, permission, allowed);
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (object instanceof RepositoryAccessControl) {
			RepositoryAccessControl other = (RepositoryAccessControl) object;
			return Objects.equals(nodeRef, other.getNodeRef())
				&& Objects.equals(authority, other.getAuthority())
				&& Objects.equals(permission, other.getPermission())
				&& Objects.equals(allowed, other.isAllowed());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nodeRef, authority, permission, allowed);
	}

}
