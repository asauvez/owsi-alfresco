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
	
	public boolean hasUserPermission(RepositoryPermission permission) {
		return repoNode.getUserPermissions().contains(permission);
	}
	public BusinessNode userPermission(RepositoryPermission permission) {
		repoNode.getUserPermissions().add(permission);
		return node;
	}
	
	public Boolean getInheritParent() {
		return repoNode.getInheritParentPermissions();
	}
	public BusinessNode inheritParent(Boolean inheritParentPermissions) {
		repoNode.setInheritParentPermissions(inheritParentPermissions);
		return node;
	}
	
	public Set<RepositoryAccessControl> getAccessControlList() {
		return repoNode.getAccessControlList();
	}
	public BusinessNode addAccessControl(RepositoryAuthority authority, RepositoryPermission permission) {
		return addAccessControl(authority, permission, true);
	}
	public BusinessNode addAccessControl(RepositoryAuthority authority, RepositoryPermission permission, boolean allowed) {
		repoNode.getAccessControlList().add(new RepositoryAccessControl(authority, permission, allowed));
		return node;
	}
}
