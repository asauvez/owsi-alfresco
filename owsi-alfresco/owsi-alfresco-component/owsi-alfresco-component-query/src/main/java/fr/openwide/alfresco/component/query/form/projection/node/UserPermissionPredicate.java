package fr.openwide.alfresco.component.query.form.projection.node;

import com.google.common.base.Predicate;

import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.model.PermissionReference;

public class UserPermissionPredicate implements Predicate<RepositoryNode>, NodeScopeInitializer {

	private final PermissionReference permission;

	public UserPermissionPredicate(PermissionReference permission) {
		this.permission = permission;
	}

	@Override
	public boolean apply(RepositoryNode node) {
		return node.getUserPermissions().contains(permission);
	}

	@Override
	public void initNodeScope(NodeScope nodeScope) {
		nodeScope.getUserPermissions().add(permission);
	}
}
