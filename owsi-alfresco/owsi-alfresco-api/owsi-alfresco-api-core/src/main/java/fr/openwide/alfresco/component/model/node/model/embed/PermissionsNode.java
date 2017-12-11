package fr.openwide.alfresco.component.model.node.model.embed;

import java.util.Set;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.node.model.RepositoryAccessControl;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.model.PermissionReference;
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
	public boolean hasUserPermission(PermissionReference permission) {
		return repoNode.getUserPermissions().contains(permission);
	}
	public boolean hasUserPermissionAddChildren() {
		return hasUserPermission(PermissionReference.ADD_CHILDREN);
	}
	public boolean hasUserPermissionWrite() {
		return hasUserPermission(PermissionReference.WRITE);
	}
	public boolean hasUserPermissionDelete() {
		return hasUserPermission(PermissionReference.DELETE);
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
	public BusinessNode addAccessControlCoordinator(AuthorityReference authority) {
		return addAccessControl(authority, PermissionReference.COORDINATOR);
	}
	public BusinessNode addAccessControlCollaborator(AuthorityReference authority) {
		return addAccessControl(authority, PermissionReference.COLLABORATOR);
	}
	public BusinessNode addAccessControlContributor(AuthorityReference authority) {
		return addAccessControl(authority, PermissionReference.CONTRIBUTOR);
	}
	public BusinessNode addAccessControlEditor(AuthorityReference authority) {
		return addAccessControl(authority, PermissionReference.EDITOR);
	}
	public BusinessNode addAccessControlConsumer(AuthorityReference authority) {
		return addAccessControl(authority, PermissionReference.CONSUMER);
	}
	
	public BusinessNode addAccessControl(AuthorityReference authority, PermissionReference permission) {
		return addAccessControl(authority, permission, true);
	}
	public BusinessNode addAccessControl(AuthorityReference authority, PermissionReference permission, boolean allowed) {
		repoNode.getAccessControlList().add(new RepositoryAccessControl(node.getNodeReference(), authority, permission, allowed));
		return node;
	}
}
