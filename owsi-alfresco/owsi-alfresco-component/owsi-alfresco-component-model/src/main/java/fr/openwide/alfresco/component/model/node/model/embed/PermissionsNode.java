package fr.openwide.alfresco.component.model.node.model.embed;

import java.util.Set;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.node.model.RepositoryAccessControl;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.model.RepositoryPermission;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;

public class PermissionsNode {

	private final BusinessNode node;
	private final RepositoryNode repoNode;
	
	public PermissionsNode(BusinessNode node) {
		this.node = node;
		this.repoNode = node.getRepositoryNode();
	}
	
	/**
	 * @return true if the current user has the given permission.
	 */
	public boolean hasUserPermission(RepositoryPermission permission) {
		return repoNode.getUserPermissions().contains(permission);
	}
	public boolean hasUserPermissionAddChildren() {
		return hasUserPermission(RepositoryPermission.ADD_CHILDREN);
	}
	public boolean hasUserPermissionWrite() {
		return hasUserPermission(RepositoryPermission.WRITE);
	}
	public boolean hasUserPermissionDelete() {
		return hasUserPermission(RepositoryPermission.DELETE);
	}
	
	public Boolean getInheritParent() {
		return repoNode.getInheritParentPermissions();
	}
	public BusinessNode inheritParent(Boolean inheritParentPermissions) {
		repoNode.setInheritParentPermissions(inheritParentPermissions);
		return node;
	}
	
	/**
	 * @return All the permissions for all users.
	 */
	public Set<RepositoryAccessControl> getAccessControlList() {
		return repoNode.getAccessControlList();
	}
	public BusinessNode addAccessControlCoordinator(RepositoryAuthority authority) {
		return addAccessControl(authority, RepositoryPermission.COORDINATOR);
	}
	public BusinessNode addAccessControlCollaborator(RepositoryAuthority authority) {
		return addAccessControl(authority, RepositoryPermission.COLLABORATOR);
	}
	public BusinessNode addAccessControlContributor(RepositoryAuthority authority) {
		return addAccessControl(authority, RepositoryPermission.CONTRIBUTOR);
	}
	public BusinessNode addAccessControlEditor(RepositoryAuthority authority) {
		return addAccessControl(authority, RepositoryPermission.EDITOR);
	}
	public BusinessNode addAccessControlConsumer(RepositoryAuthority authority) {
		return addAccessControl(authority, RepositoryPermission.CONSUMER);
	}
	
	public BusinessNode addAccessControl(RepositoryAuthority authority, RepositoryPermission permission) {
		return addAccessControl(authority, permission, true);
	}
	public BusinessNode addAccessControl(RepositoryAuthority authority, RepositoryPermission permission, boolean allowed) {
		repoNode.getAccessControlList().add(new RepositoryAccessControl(node.getNodeReference(), authority, permission, allowed));
		return node;
	}
}
