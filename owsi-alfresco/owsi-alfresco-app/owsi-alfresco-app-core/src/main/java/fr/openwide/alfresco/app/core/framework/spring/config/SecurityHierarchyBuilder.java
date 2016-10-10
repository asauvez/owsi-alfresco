package fr.openwide.alfresco.app.core.framework.spring.config;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.core.jpa.security.model.NamedPermission;

public class SecurityHierarchyBuilder {

	private StringBuilder sb = new StringBuilder();
	
	public SecurityHierarchyBuilder add(String ifRole, String ... thenRoles) {
		for (String thenRole : thenRoles) {
			sb.append(ifRole).append(" > ").append(thenRole).append("\n");
		}
		return this;
	}
	public SecurityHierarchyBuilder add(RepositoryAuthority ifRole, RepositoryAuthority ... thenRoles) {
		for (RepositoryAuthority thenRole : thenRoles) {
			sb.append(ifRole.getName()).append(" > ").append(thenRole.getName()).append("\n");
		}
		return this;
	}
	
	public SecurityHierarchyBuilder add(NamedPermission ifPermission, NamedPermission ... thenPermissions) {
		for (NamedPermission thenPermission : thenPermissions) {
			sb.append(ifPermission.getName()).append(" > ").append(thenPermission.getName()).append("\n");
		}
		return this;
	}
	
	public String build() {
		return sb.toString();
	}
}
