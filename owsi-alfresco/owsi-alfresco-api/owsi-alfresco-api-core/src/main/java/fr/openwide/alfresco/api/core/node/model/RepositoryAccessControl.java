package fr.openwide.alfresco.api.core.node.model;

import java.io.Serializable;
import java.util.Objects;

import com.google.common.base.Joiner;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;

public class RepositoryAccessControl implements Serializable {

	private static final long serialVersionUID = 1391171626983929282L;

	private RepositoryAuthority authority;
	private RepositoryPermission permission;
	private boolean allowed;

	public RepositoryAccessControl() {}

	public RepositoryAccessControl(RepositoryAuthority authority, RepositoryPermission permission, boolean allowed) {
		this.authority = authority;
		this.permission = permission;
		this.allowed = allowed;
	}

	public RepositoryAuthority getAuthority() {
		return authority;
	}
	public void setAuthority(RepositoryAuthority authority) {
		this.authority = authority;
	}

	public RepositoryPermission getPermission() {
		return permission;
	}
	public void setPermission(RepositoryPermission permission) {
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
		return Joiner.on(":").join(authority, permission, allowed);
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
			return Objects.equals(authority, other.getAuthority())
				&& Objects.equals(permission, other.getPermission())
				&& Objects.equals(allowed, other.isAllowed());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(authority, permission, allowed);
	}

}
