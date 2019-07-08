package fr.openwide.alfresco.repo.dictionary.permission.service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.alfresco.service.cmr.repository.NodeRef;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.node.model.PermissionReference;
import fr.openwide.alfresco.api.core.node.model.RepositoryAccessControl;

public interface PermissionRepositoryService {

	boolean hasPermission(NodeRef nodeRef, PermissionReference permission);
	
	void setInheritParentPermissions(NodeRef nodeRef, boolean inheritParentPermissions);
	public void setPermission(NodeRef nodeRef, AuthorityReference authority, PermissionReference permission);
	public void setPermission(NodeRef nodeRef, AuthorityReference authority, PermissionReference permission, boolean allowed);
	
	void deletePermission(NodeRef nodeRef, AuthorityReference authority, PermissionReference permission);
	void deletePermissions(NodeRef nodeRef);
	
	List<RepositoryAccessControl> searchACL(NodeRef nodeRef, boolean inherited);
	List<RepositoryAccessControl> searchACL(AuthorityReference authorityReference);
	List<RepositoryAccessControl> searchACLwithParentAuthorities(AuthorityReference authorityReference);
	List<RepositoryAccessControl> searchACL(Collection<AuthorityReference> authorityReferences);
	
	int replaceAuthority(AuthorityReference oldAuthority, AuthorityReference newAuthority, boolean removeOldInSite, boolean deactivateOldUser);
	int replaceAuthority(AuthorityReference oldAuthority, AuthorityReference newAuthority, Optional<Integer> maxItem, boolean removeOldInSite, boolean deactivateOldUser);

}
