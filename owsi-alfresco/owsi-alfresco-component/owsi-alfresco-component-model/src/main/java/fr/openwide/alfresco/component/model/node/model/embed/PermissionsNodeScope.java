package fr.openwide.alfresco.component.model.node.model.embed;

import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryPermission;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;

public class PermissionsNodeScope {

	private final NodeScopeBuilder builder;
	private final NodeScope scope;
	
	public PermissionsNodeScope(NodeScopeBuilder builder) {
		this.builder = builder;
		this.scope = builder.getScope();
	}

	
	public NodeScopeBuilder userPermission(RepositoryPermission permission) {
		scope.getUserPermissions().add(permission);
		return builder;
	}
	public NodeScopeBuilder accessPermissions() {
		scope.setAccessPermissions(true);
		return builder;
	}
}
