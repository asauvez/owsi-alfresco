package fr.openwide.alfresco.repo.dictionary.permission.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.node.model.PermissionReference;
import fr.openwide.alfresco.api.core.node.model.RepositoryAccessControl;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;

public interface PermissionRepositoryService {

	boolean hasPermission(NodeReference nodeReference, PermissionReference permission);
	
	void setInheritParentPermissions(NodeReference nodeReference, boolean inheritParentPermissions);
	public void setPermission(NodeReference nodeReference, AuthorityReference authority, PermissionReference permission);
	public void setPermission(NodeReference nodeReference, AuthorityReference authority, PermissionReference permission, boolean allowed);
	
	void deletePermission(NodeReference nodeReference, AuthorityReference authority, PermissionReference permission);
	void deletePermissions(NodeReference nodeReference);
	
	List<RepositoryAccessControl> searchACL(AuthorityReference authorityReference);
	List<RepositoryAccessControl> searchACLwithParentAuthorities(AuthorityReference authorityReference);
	List<RepositoryAccessControl> searchACL(Collection<AuthorityReference> authorityReferences);
	
	int replaceAuthority(AuthorityReference oldAuthority, AuthorityReference newAuthority, boolean removeOldInSite, boolean deactivateOldUser);
	int replaceAuthority(AuthorityReference oldAuthority, AuthorityReference newAuthority, Optional<Integer> maxItem, boolean removeOldInSite, boolean deactivateOldUser);

}
