package fr.openwide.alfresco.query.web.form.projection.node;

import com.google.common.base.Predicate;

import fr.openwide.alfresco.query.api.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.api.search.model.NodeResult;

public class UserPermissionPredicate implements Predicate<NodeResult>, NodeFetchDetailsInitializer {

	private final String permission;

	public UserPermissionPredicate(String permission) {
		this.permission = permission;
	}

	@Override
	public boolean apply(NodeResult node) {
		return node.getUserPermissions().contains(permission);
	}

	@Override
	public void initNodeFetchDetails(NodeFetchDetails nodeFetchDetails) {
		nodeFetchDetails.getUserPermissions().add(permission);
	}
}
