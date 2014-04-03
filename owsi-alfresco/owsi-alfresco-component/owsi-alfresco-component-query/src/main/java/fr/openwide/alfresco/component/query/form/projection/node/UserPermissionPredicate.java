package fr.openwide.alfresco.component.query.form.projection.node;

import com.google.common.base.Predicate;

import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.model.RepositoryPermission;

public class UserPermissionPredicate implements Predicate<RepositoryNode>, NodeScopeInitializer {

	private final RepositoryPermission permission;

	public UserPermissionPredicate(RepositoryPermission permission) {
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
