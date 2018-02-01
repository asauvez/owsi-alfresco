package fr.openwide.alfresco.repo.dictionary.permission.service.impl;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.alfresco.enterprise.repo.authorization.AuthorizationService;
import org.alfresco.service.cmr.preference.PreferenceService;
import org.alfresco.service.cmr.security.AccessStatus;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.node.model.PermissionReference;
import fr.openwide.alfresco.api.core.node.model.RepositoryAccessControl;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.search.model.SearchQueryBuilder;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.dictionary.permission.service.PermissionRepositoryService;
import fr.openwide.alfresco.repo.dictionary.policy.service.PolicyRepositoryService;
import fr.openwide.alfresco.repo.dictionary.search.model.BatchSearchQueryBuilder;
import fr.openwide.alfresco.repo.dictionary.search.service.NodeSearchModelRepositoryService;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;

public class PermissionRepositoryServiceImpl implements PermissionRepositoryService {

	private final Logger LOGGER = LoggerFactory.getLogger(PermissionRepositoryServiceImpl.class);
	
	private PermissionService permissionService;
	private AuthorityService authorityService;
	private ConversionService conversionService;
	private NodeModelRepositoryService nodeModelService;
	private NodeSearchModelRepositoryService nodeSearchModelService;
	private PolicyRepositoryService policyRepositoryService;
	private PreferenceService preferenceService;
	private PersonService personService;
	private AuthorizationService authorizationService;
    
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
	public int replaceAuthority(AuthorityReference oldAuthority, AuthorityReference newAuthority, boolean removeOldInSite, boolean deactivateOldUser) {
		return replaceAuthority(oldAuthority, newAuthority, Optional.empty(), removeOldInSite, deactivateOldUser);
	}
	@Override
	public int replaceAuthority(AuthorityReference oldAuthority, AuthorityReference newAuthority, Optional<Integer> maxItem, boolean removeOldInSite, boolean deactivateOldUser) {
		String oldAuthorityName = oldAuthority.getName();
		if (!authorityService.authorityExists(oldAuthorityName)) {
			throw new IllegalArgumentException(oldAuthorityName + " doesn't exist");
		}
		String newAuthorityName = newAuthority.getName();
		if (!authorityService.authorityExists(newAuthorityName)) {
			throw new IllegalArgumentException(newAuthorityName + " doesn't exist");
		}
		// Ajoute les groupes parents de l'ancien dans le nouveau : Typiquement les groupes de site : site_toto_SiteCollaborator
		Set<String> containingAuthoritiesOld = authorityService.getContainingAuthorities(AuthorityType.GROUP, oldAuthorityName, true);
		Set<String> containingAuthoritiesNew = authorityService.getContainingAuthorities(AuthorityType.GROUP, newAuthorityName, true);
		
		Set<String> containerAuthorities  = new TreeSet<>(containingAuthoritiesOld);
		containerAuthorities.removeAll(containingAuthoritiesNew);
		for (String containerAuthority : containerAuthorities) {
			authorityService.addAuthority(containerAuthority, newAuthorityName);
		}
		// Supprime les références à l'ancienne authorité dans les groupes parents
		if (removeOldInSite) {
			LOGGER.info("Removing old authority from site containers");
			for (String containerAuthorityOld: containingAuthoritiesOld) {
				authorityService.removeAuthority(containerAuthorityOld, oldAuthorityName);
			}
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
		
		if (   AuthorityType.getAuthorityType(oldAuthorityName) == AuthorityType.USER 
			&& AuthorityType.getAuthorityType(newAuthorityName) == AuthorityType.USER) {
			List<TextPropertyModel> propertyModels = Arrays.asList(CmModel.ownable.owner, CmModel.auditable.creator, CmModel.auditable.modifier);
			Integer newMax = null;
			for (TextPropertyModel propertyModel: propertyModels) {
				newMax = maxItem.isPresent() ? maxItem.get() - cpt : null;
				RestrictionBuilder restrictionOnUserNode = new RestrictionBuilder().eq(propertyModel, oldAuthorityName).of();
				Consumer<NodeReference> changePropPrivileged =  disableBehaviour(getChangePropConsumer(newAuthorityName, propertyModel));
				cpt += searchAndApply(restrictionOnUserNode, changePropPrivileged, newMax);
			}
			
			// Deactivate old user before moving home
			if ( deactivateOldUser ) {
				LOGGER.info(String.format("Deauthorize old user before moving home : %s", oldAuthorityName));
				if (authorityService.authorityExists(oldAuthorityName)){
					deauthorizeUser(oldAuthorityName);
				}
			}
			
			// If maxItem is reached, skip copyHome, so we can do it on next iteration
			if (!maxItem.isPresent() || cpt <= maxItem.get()) {
				copyHomeFilesAndPreferences(oldAuthorityName, newAuthorityName);
			}
			
		}
		
		return cpt;
	}

	private void deauthorizeUser(String oldAuthorityName) {
		try{
			authorizationService.deauthorize(oldAuthorityName);
			LOGGER.warn(String.format("%s has been deauthorized", oldAuthorityName));
		} catch (RuntimeException e) {
			// get the AuthorizationException (which is private)
			LOGGER.warn(String.format("failed to deauthorize user %s : %s", oldAuthorityName, e.getMessage()));
		}
	}

	private NodeReference copyHomeFilesAndPreferences(String sourceUserName, String targetUserName) {
		NodeReference sourceUserNode = conversionService.get(personService.getPerson(sourceUserName, false));
		NodeReference targetUserNode = conversionService.get(personService.getPerson(targetUserName, false));
		NodeReference sourceFolder = nodeModelService.getProperty(sourceUserNode, CmModel.person.homeFolder);
		
		// The preferences copy must run only once
		if (sourceFolder != null) {
			copyPreferences(sourceUserName, targetUserName);
		}
		
		boolean isSourceFolderEmpty = isFolderEmpty(sourceFolder);
		if (isSourceFolderEmpty) {
			// Nothing to do
			return targetUserNode;
		}
		NodeReference targetFolder = nodeModelService.getProperty(targetUserNode, CmModel.person.homeFolder);
	
		// If the target user folder doesn't exist, the home folder is replaced, else a subfolder is created inside the home folder named copy-<sourceFolder> 
		boolean replaceHomeFolder = isFolderEmpty(targetFolder);
		if (replaceHomeFolder) {
			LOGGER.info(String.format("copyHomeFolder: home for target user %s is empty, renaming %s", targetUserName, sourceUserName));
			Optional<NodeReference> userHomes = nodeModelService.getPrimaryParent(sourceFolder);
			if (!userHomes.isPresent()) {
				throw new IllegalStateException("The user homes is not present, something is wrong");
			}
			// Si le dossier cible existe, il est vide: on le supprime pour pouvoir renommer le dossier source
			if (targetFolder != null && nodeModelService.exists(targetFolder)) {
				nodeModelService.delete(targetFolder);
			}
			targetFolder = userHomes.get();
			nodeModelService.setProperty(sourceFolder, CmModel.folder.name, targetUserName);
			nodeModelService.setProperty(targetUserNode, CmModel.person.homeFolder, sourceFolder);
			targetFolder = sourceFolder;
		}
		else {
			LOGGER.info(String.format("copyHomeFolder: home for target user %s is not empty, copying %s inside a copy folder", targetUserName, sourceUserName));
			String folderName = nodeModelService.getProperty(sourceFolder, CmModel.folder.name);
			Optional<String> copyFolderName = Optional.of("copy-" + folderName);
			nodeModelService.copy(sourceFolder, targetFolder, copyFolderName);
			//nodeModelService.getChildrenAssocsContains(sourceFolder).forEach(node -> nodeModelService.delete(node));
			nodeModelService.delete(sourceFolder);
		}
		nodeModelService.setProperty(sourceUserNode, CmModel.person.homeFolder, null);
		return targetFolder;
	}

	private void copyPreferences(String oldAuthorityName, String newAuthorityName) {
		Map<String, Serializable> newPreferences = preferenceService.getPreferences(oldAuthorityName);
		preferenceService.setPreferences(newAuthorityName, newPreferences);
	}
	
	private boolean isFolderEmpty(NodeReference folderNode) {
		return folderNode == null || !nodeModelService.exists(folderNode) || nodeModelService.getChildren(folderNode, new NodeScopeBuilder()).isEmpty();
	}
	
	public int searchAndApply(RestrictionBuilder restriction, Consumer<NodeReference> consumer, Integer maxItem) {
		SearchQueryBuilder searchBuilder = new BatchSearchQueryBuilder()
				.configurationName("userReplaceSearch")
				.consumer(consumer)
				.restriction(restriction)
				.maxResults(maxItem);
		return nodeSearchModelService.searchBatch((BatchSearchQueryBuilder) searchBuilder);
	}
	
	private Consumer<NodeReference> disableBehaviour(Consumer<NodeReference> consumer) {
		return nodeReference ->  policyRepositoryService.disableBehaviour(CmModel.auditable, () -> consumer.accept(nodeReference));
	}

	private Consumer<NodeReference> getChangePropConsumer(String propName, TextPropertyModel propertyModel) {
		return nodeReference -> {nodeModelService.setProperty(nodeReference, propertyModel, propName);};
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
	public void setPreferenceService(PreferenceService preferenceService) {
		this.preferenceService = preferenceService;
	}
	public void setPolicyRepositoryService(PolicyRepositoryService policyRepositoryService) {
		this.policyRepositoryService = policyRepositoryService;
	}
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
	
	public void setAuthorizationService(AuthorizationService authorizationService) {
		this.authorizationService = authorizationService;
	}
	
}
