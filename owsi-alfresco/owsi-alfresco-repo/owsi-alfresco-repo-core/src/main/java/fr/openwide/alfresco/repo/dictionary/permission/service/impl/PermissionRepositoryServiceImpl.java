package fr.openwide.alfresco.repo.dictionary.permission.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.PermissionService;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.node.model.PermissionReference;
import fr.openwide.alfresco.api.core.node.model.RepositoryAccessControl;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.search.model.SearchQueryBuilder;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.dictionary.permission.service.PermissionRepositoryService;
import fr.openwide.alfresco.repo.dictionary.search.service.NodeSearchModelRepositoryService;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;

public class PermissionRepositoryServiceImpl implements PermissionRepositoryService {

	private PermissionService permissionService;
	private AuthorityService authorityService;

	private ConversionService conversionService;
	private NodeModelRepositoryService nodeModelService;
	private NodeSearchModelRepositoryService nodeSearchModelService;

	private DataSource dataSource;
	
	@Override
	public boolean hasPermission(NodeReference nodeReference, PermissionReference permission) {
		return permissionService.hasPermission(conversionService.getRequired(nodeReference), permission.getName()) == AccessStatus.ALLOWED;
	}
	
	@Override
	public void setInheritParentPermissions(NodeReference nodeReference, boolean inheritParentPermissions) {
		permissionService.setInheritParentPermissions(conversionService.getRequired(nodeReference), inheritParentPermissions);
	}
	@Override
	public void setPermission(NodeReference nodeReference, AuthorityReference authority, PermissionReference permission) {
		setPermission(nodeReference, authority, permission, true);
	}
	@Override
	public void setPermission(NodeReference nodeReference, AuthorityReference authority, PermissionReference permission, boolean allowed) {
		permissionService.setPermission(conversionService.getRequired(nodeReference), authority.getName(), permission.getName(), allowed);
	}
	@Override
	public void deletePermission(NodeReference nodeReference, AuthorityReference authority, PermissionReference permission) {
		permissionService.deletePermission(conversionService.getRequired(nodeReference), authority.getName(), permission.getName());
	}
	
	@Override
	public List<RepositoryAccessControl> searchACL(AuthorityReference authorityReference) {
		String sql = "SELECT store.protocol, store.identifier, an.uuid, alfauth.authority, aperm.name as Permission, ace.allowed "
				+ "FROM alf_node an, alf_acl_member acl_m, alf_access_control_entry ace, alf_authority alfauth, alf_access_control_list acl, alf_permission aperm, alf_store store "
				+ "WHERE an.acl_id = acl_m.acl_id "
				+ "  AND acl_m.ace_id = ace.id "
				+ "  AND alfauth.id = ace.authority_id"
				+ "  AND acl.id = acl_m.acl_id"
				+ "  AND aperm.id = ace.permission_id"
				+ "  AND store.id = an.store_id"
				+ "  AND acl_m.pos = 0"
				+ "  AND alfauth.authority = ?";
		try (Connection conn = dataSource.getConnection()) {
			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, authorityReference.getName());
				
				try (ResultSet res = stmt.executeQuery()) {
					List<RepositoryAccessControl> list = new ArrayList<>();
					while(res.next()) {
						int col = 1;
						NodeReference nodeReference = NodeReference.create(res.getString(col ++), res.getString(col ++), res.getString(col ++));
						AuthorityReference authority = AuthorityReference.authority(res.getString(col ++));
						PermissionReference permission = PermissionReference.create(res.getString(col ++));
						boolean allowed = res.getBoolean(col ++);
						
						list.add(new RepositoryAccessControl(nodeReference, authority, permission, allowed));
					}
					return list;
				}
			}
		} catch (SQLException e) {
			throw new IllegalStateException(sql, e);
		}
	}
	
	@Override
	public int replaceAuthority(AuthorityReference oldAuthority, AuthorityReference newAuthority) {
		return replaceAuthority(oldAuthority, newAuthority, Optional.empty());
	}
	@Override
	public int replaceAuthority(AuthorityReference oldAuthority, AuthorityReference newAuthority, Optional<Integer> maxItem) {
		// Ajoute les groupes parents de l'ancien dans le nouveau : Typiquement les groupes de site : site_toto_SiteCollaborator
		Set<String> containedAuthoritiesOld = authorityService.getContainedAuthorities(AuthorityType.GROUP, oldAuthority.getName(), true);
		Set<String> containedAuthoritiesNew = authorityService.getContainedAuthorities(AuthorityType.GROUP, newAuthority.getName(), true);
		Set<String> authoritiesToAdd  = new TreeSet<>(containedAuthoritiesOld);
		authoritiesToAdd.removeAll(containedAuthoritiesNew);
		for (String authorityToAdd : authoritiesToAdd) {
			authorityService.addAuthority(authorityToAdd, newAuthority.getName());
		}

		// Remplace l'authority dans les ACL
		int cpt = 0;
		List<RepositoryAccessControl> listAcl = searchACL(oldAuthority);
		for (RepositoryAccessControl acl : listAcl) {
			if (maxItem.isPresent() && maxItem.get() == cpt) {
				break;
			}
			setPermission(acl.getNodeReference(), newAuthority, acl.getPermission(), acl.isAllowed());
			deletePermission(acl.getNodeReference(), oldAuthority, acl.getPermission());
			cpt ++;
		}
		
		if (   AuthorityType.getAuthorityType(oldAuthority.getName()) == AuthorityType.USER 
			&& AuthorityType.getAuthorityType(newAuthority.getName()) == AuthorityType.USER) {
			List<NodeReference> listNodeOwner = nodeSearchModelService.searchReference(new SearchQueryBuilder()
					.restriction(new RestrictionBuilder()
							.eq(CmModel.ownable.owner, oldAuthority.getName()).of())
					.maxResults(maxItem.orElse(null)));
			for (NodeReference nodeReference : listNodeOwner) {
				if (maxItem.isPresent() && maxItem.get() == cpt) {
					break;
				}
				nodeModelService.setProperty(nodeReference, CmModel.ownable.owner, newAuthority.getName());
				cpt ++;
			}
		}
		
		return cpt;
	}
	
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	public void setNodeModelService(NodeModelRepositoryService nodeModelService) {
		this.nodeModelService = nodeModelService;
	}
	public void setNodeSearchModelService(NodeSearchModelRepositoryService nodeSearchModelService) {
		this.nodeSearchModelService = nodeSearchModelService;
	}
	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}
}
