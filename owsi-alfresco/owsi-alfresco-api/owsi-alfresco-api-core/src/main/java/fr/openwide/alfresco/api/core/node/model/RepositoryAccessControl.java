package fr.openwide.alfresco.api.core.node.model;

import java.io.Serializable;
import java.util.Objects;

import com.google.common.base.Joiner;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;

public class RepositoryAccessControl implements Serializable {

	private NodeReference nodeReference;
	private AuthorityReference authority;
	private PermissionReference permission;
	private boolean allowed;

	public RepositoryAccessControl() {}

	public RepositoryAccessControl(NodeReference nodeReference, AuthorityReference authority, PermissionReference permission, boolean allowed) {
		this.nodeReference = nodeReference;
		this.authority = authority;
		this.permission = permission;
		this.allowed = allowed;
	}

	public NodeReference getNodeReference() {
		return nodeReference;
	}
	public void setNodeReference(NodeReference nodeReference) {
		this.nodeReference = nodeReference;
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

	@Override
	public String toString() {
		return Joiner.on(":").join(nodeReference, authority, permission, allowed);
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
			return Objects.equals(nodeReference, other.getNodeReference())
				&& Objects.equals(authority, other.getAuthority())
				&& Objects.equals(permission, other.getPermission())
				&& Objects.equals(allowed, other.isAllowed());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nodeReference, authority, permission, allowed);
	}

}
