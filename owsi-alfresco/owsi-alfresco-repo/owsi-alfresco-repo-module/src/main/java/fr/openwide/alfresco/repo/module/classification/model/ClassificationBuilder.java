package fr.openwide.alfresco.repo.module.classification.model;

import java.util.Arrays;

import org.alfresco.repo.security.authentication.AuthenticationUtil;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.module.model.OwsiModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repo.module.classification.service.impl.ClassificationServiceImpl;

/**
 * Utilitaire permettant de choisir l'emplacement racine de la classification.
 */
public class ClassificationBuilder {

	private final ClassificationServiceImpl service;
	private final BusinessNode node;

	public ClassificationBuilder(ClassificationServiceImpl service, BusinessNode node) {
		this.service = service;
		this.node = node;
	}
	
	/** 
	 * Défini un noeud racine de la classification. 
	 * Si on ne défini pas de noeud racine, la classification se fait à partir de l'emplacement actuel.
	 */
	public ClassificationWithRootBuilder rootFolder(NodeReference destinationFolder) {
		return new ClassificationWithRootBuilder(service, node, destinationFolder);
	}
	/**
	 * Défini le noeud racine comme étant le résultat unique que renvoi la requête fournie.
	 */
	public ClassificationWithRootBuilder rootFolder(RestrictionBuilder restrictionBuilder) {
		return rootFolder(restrictionBuilder, false);
	}
	
	/**
	 * @param cached Si vrai, alors mets en cache mémoire le résultat de la recherche.
	 */
	public ClassificationWithRootBuilder rootFolder(RestrictionBuilder restrictionBuilder, boolean cached) {
		Optional<NodeReference> optional = (cached) 
				? service.searchUniqueReferenceCached(restrictionBuilder) 
				: service.searchUniqueReference(restrictionBuilder);
		if (! optional.isPresent()) {
			throw new NoSuchNodeRemoteException(restrictionBuilder.toFtsQuery());
		}
		return rootFolder(optional.get());
	}
	
	public ClassificationWithRootBuilder rootFolderPath(String path) {
		return rootFolder(new RestrictionBuilder()
				.path(path).of(), true);
	}
	public ClassificationWithRootBuilder rootActualFolder() {
		return rootFolder(node.assocs().primaryParent().getNodeReference());
	}

	/**
	 * Recherche un noeud racine à partir d'un chemin. Le chemin est défini en dessous de "Company Home",
	 * avec une liste de cm:name. 
	 */
	public ClassificationWithRootBuilder rootFolderNamedPath(String ... names) {
		return rootFolderNamedPath(true, names);
	}
	public ClassificationWithRootBuilder rootFolderNamedPath(boolean cached, String ... names) {
		Optional<NodeReference> optional = (cached) 
				? service.getByNamedPathCached(names)
				: service.getByNamedPath(names);
		if (! optional.isPresent()) {
			throw new NoSuchNodeRemoteException(Arrays.toString(names));
		}
		return rootFolder(optional.get());
	}
	
	/**
	 * Recherche un noeud racine identifié par un owsi:identifiable. 
	 */
	public ClassificationWithRootBuilder rootFolderIdentifier(NameReference identifier) {
		return rootFolder(new RestrictionBuilder()
				.eq(OwsiModel.identifiable.identifier, identifier).of(), true);
	}
	
	/** 
	 * Défini le noeud racine de la classification comme étant le home folder de l'utilisateur en cours. 
	 */
	public ClassificationWithRootBuilder rootHomeFolder() {
		Optional<NodeReference> homeFolder = service.getHomeFolder();
		if (! homeFolder.isPresent()) {
			throw new IllegalStateException("User " + AuthenticationUtil.getRunAsUser() + " does not have a home folder.");
		}
		return rootFolder(homeFolder.get());
	}
	
	public ClassificationBuilder unlinkSecondaryParents() {
		return unlinkSecondaryParents(CmModel.folder.contains);
	}
	public ClassificationBuilder unlinkSecondaryParents(ChildAssociationModel childAssociationModel) {
		service.deleteSecondaryParents(node.getNodeReference(), childAssociationModel);
		return this;
	}
}
