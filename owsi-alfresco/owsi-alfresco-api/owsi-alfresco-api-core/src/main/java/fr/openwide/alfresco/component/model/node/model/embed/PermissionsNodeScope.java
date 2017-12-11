package fr.openwide.alfresco.component.model.node.model.embed;

import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.PermissionReference;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;

public class PermissionsNodeScope {

	private final NodeScopeBuilder builder;
	private final NodeScope scope;
	
	public PermissionsNodeScope(NodeScopeBuilder builder) {
		this.builder = builder;
		this.scope = builder.getScope();
	}
	
	public NodeScopeBuilder userPermission(PermissionReference permission) {
		scope.getUserPermissions().add(permission);
		return builder;
	}
	public NodeScopeBuilder userPermissionAddChildren() {
		return userPermission(PermissionReference.ADD_CHILDREN);
	}
	public NodeScopeBuilder userPermissionWrite() {
		return userPermission(PermissionReference.WRITE);
	}
	public NodeScopeBuilder userPermissionDelete() {
		return userPermission(PermissionReference.DELETE);
	}
	
	public NodeScopeBuilder accessPermissions() {
		scope.setAccessPermissions(true);
		return builder;
	}
}
