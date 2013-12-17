package fr.openwide.alfresco.query.web.form.projection.node;

import com.google.common.base.Predicate;

import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.model.RepositoryPermission;

public class UserPermissionPredicate implements Predicate<RepositoryNode>, NodeFetchDetailsInitializer {

	private final RepositoryPermission permission;

	public UserPermissionPredicate(RepositoryPermission permission) {
		this.permission = permission;
	}

	@Override
	public boolean apply(RepositoryNode node) {
		return node.getUserPermissions().contains(permission);
	}

	@Override
	public void initNodeFetchDetails(NodeFetchDetails nodeFetchDetails) {
		nodeFetchDetails.getUserPermissions().add(permission);
	}
}
