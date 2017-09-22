package fr.openwide.alfresco.repo.dictionary.permission.service;

import java.util.List;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.node.model.PermissionReference;
import fr.openwide.alfresco.api.core.node.model.RepositoryAccessControl;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;

public interface PermissionRepositoryService {

	boolean hasPermission(NodeReference nodeReference, PermissionReference permission);
	void setInheritParentPermissions(NodeReference nodeReference, boolean inheritParentPermissions);
	public void setPermission(NodeReference nodeReference, AuthorityReference authority, PermissionReference permission);
	void deletePermission(NodeReference nodeReference, AuthorityReference authority, PermissionReference permission);
	
	List<RepositoryAccessControl> searchACL(AuthorityReference authorityReference);
}
