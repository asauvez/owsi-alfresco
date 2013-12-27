package fr.openwide.alfresco.repository.api.node.model;

import java.io.Serializable;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class RepositoryAuthorityPermission implements Serializable {

	private RepositoryAuthority authority;
	private RepositoryPermission permission;
	private boolean allowed;

	public RepositoryAuthorityPermission() {}

	public RepositoryAuthorityPermission(RepositoryAuthority authority, RepositoryPermission permission, boolean allowed) {
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
	public boolean equals(Object obj) {
		if (obj instanceof NameReference) {
			return Objects.equal(getAuthority(), ((RepositoryAuthorityPermission) obj).getAuthority())
				&& Objects.equal(getPermission(), ((RepositoryAuthorityPermission) obj).getPermission())
				&& Objects.equal(isAllowed(), ((RepositoryAuthorityPermission) obj).isAllowed());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getAuthority(), getPermission(), isAllowed());
	}
}
