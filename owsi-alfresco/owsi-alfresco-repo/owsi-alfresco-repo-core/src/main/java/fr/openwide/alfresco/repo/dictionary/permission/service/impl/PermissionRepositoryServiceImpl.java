package fr.openwide.alfresco.repo.dictionary.permission.service.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.PermissionService;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.node.model.PermissionReference;
import fr.openwide.alfresco.api.core.node.model.RepositoryAccessControl;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.repo.dictionary.permission.service.PermissionRepositoryService;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;

public class PermissionRepositoryServiceImpl implements PermissionRepositoryService {

	private PermissionService permissionService;
	private ConversionService conversionService;
	
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
		permissionService.setPermission(conversionService.getRequired(nodeReference), authority.getName(), permission.getName(), true);
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
	
	public void setPermissionService(PermissionService permissionService) {
		this.permissionService = permissionService;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
