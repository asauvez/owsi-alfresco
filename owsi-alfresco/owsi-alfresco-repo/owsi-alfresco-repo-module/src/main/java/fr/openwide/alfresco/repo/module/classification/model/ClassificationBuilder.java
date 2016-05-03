package fr.openwide.alfresco.repo.module.classification.model;

import java.util.Arrays;

import org.alfresco.repo.security.authentication.AuthenticationUtil;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.module.model.OwsiModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repo.module.classification.service.impl.ClassificationServiceImpl;

/**
 * Utilitaire permettant de construire la classification.
 */
public class ClassificationBuilder {

	private final ClassificationServiceImpl service;
	private final BusinessNode node;
	private NodeReference actualParentFolder;
	private NodeReference destinationFolder;

	public ClassificationBuilder(ClassificationServiceImpl service, BusinessNode node) {
		this.service = service;
		this.node = node;
		this.actualParentFolder = node.assocs().primaryParent().getNodeReference();
		this.destinationFolder = actualParentFolder;
	}
	
	/** 
	 * Défini un noeud racine de la classification. 
	 * Si on ne défini pas de noeud racine, la classification se fait à partir de l'emplacement actuel.
	 */
	public ClassificationBuilder rootFolder(NodeReference destinationFolder) {
		this.destinationFolder = destinationFolder;
		return this;
	}
	/**
	 * Défini le noeud racine comme étant le résultat unique que renvoi la requête fournie.
	 */
	public ClassificationBuilder rootFolder(RestrictionBuilder restrictionBuilder) {
		return rootFolder(restrictionBuilder, false);
	}
	
	/**
	 * @param cached Si vrai, alors mets en cache mémoire le résultat de la recherche.
	 */
	public ClassificationBuilder rootFolder(RestrictionBuilder restrictionBuilder, boolean cached) {
		Optional<NodeReference> optional = (cached) 
				? service.searchUniqueReferenceCached(restrictionBuilder) 
				: service.searchUniqueReference(restrictionBuilder);
		if (! optional.isPresent()) {
			throw new NoSuchNodeRemoteException(restrictionBuilder.toQuery());
		}
		return rootFolder(optional.get());
	}
	
	public ClassificationBuilder rootFolderPath(String path) {
		return rootFolder(new RestrictionBuilder()
				.path(path).of(), true);
	}

	/**
	 * Recherche un noeud racine à partir d'un chemin. Le chemin est défini en dessous de "Company Home",
	 * avec une liste de cm:name. 
	 */
	public ClassificationBuilder rootFolderNamedPath(String ... names) {
		return rootFolderNamedPath(true, names);
	}
	public ClassificationBuilder rootFolderNamedPath(boolean cached, String ... names) {
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
	public ClassificationBuilder rootFolderIdentifier(NameReference identifier) {
		return rootFolder(new RestrictionBuilder()
				.eq(OwsiModel.identifiable.identifier, identifier).of(), true);
	}
	
	/** 
	 * Défini le noeud racine de la classification comme étant le home folder de l'utilisateur en cours. 
	 */
	public ClassificationBuilder rootHomeFolder() {
		Optional<NodeReference> homeFolder = service.getHomeFolder();
		if (! homeFolder.isPresent()) {
			throw new IllegalStateException("User " + AuthenticationUtil.getRunAsUser() + " does not have a home folder.");
		}
		rootFolder(homeFolder.get());
		return this;
	}
	
	/**
	 * Créer un sous dossier par rapport au noeud en cours, si ce dossier n'existe pas encore.
	 */
	public ClassificationBuilder subFolder(String folderName) {
		return subFolder(new BusinessNode()
			.properties().name(folderName));
	}
	/**
	 * @param folderNode Si le dossier n'existe pas encore, on va le créer avec le type, les propriétés et les permissions
	 * du noeud fourni.
	 */
	public ClassificationBuilder subFolder(BusinessNode folderNode) {
		folderNode.aspect(OwsiModel.deleteIfEmpty);
		destinationFolder = service.subFolder(folderNode, destinationFolder);
		return this;
	}

	public ClassificationBuilder subFolder(SubFolderBuilder subFolderBuilder) {
		return subFolder(subFolderBuilder, new BusinessNode());
	}
	public ClassificationBuilder subFolder(SubFolderBuilder subFolderBuilder, BusinessNode folderNode) {
		String folderName = subFolderBuilder.getFolderName(node);
		return subFolder(folderNode
				.properties().name(folderName));
	}

	/**
	 * Créer un sous dossier avec comme nom la valeur d'une propriété du noeud à classer.
	 */
	public ClassificationBuilder subFolderProperty(SinglePropertyModel<?> property) {
		return subFolder(new SubFolderBuilder(property));
	}
	public ClassificationBuilder subFolderProperty(SinglePropertyModel<?> property, BusinessNode folderNode) {
		return subFolder(new SubFolderBuilder(property), folderNode);
	}

	public ClassificationBuilder subFolderYear() {
		return subFolder(new SubFolderBuilder(CmModel.auditable.created)
			.formatYear());
	}
	public ClassificationBuilder subFolderMonth() {
		return subFolder(new SubFolderBuilder(CmModel.auditable.created)
			.formatMonth());
	}
	public ClassificationBuilder subFolderDay() {
		return subFolder(new SubFolderBuilder(CmModel.auditable.created)
			.formatDay());
	}
	public ClassificationBuilder subFolderHour() {
		return subFolder(new SubFolderBuilder(CmModel.auditable.created)
			.formatHour());
	}
	public ClassificationBuilder subFolderMinute() {
		return subFolder(new SubFolderBuilder(CmModel.auditable.created)
			.formatMinute());
	}
	
	public NodeReference getDestinationFolder() {
		return destinationFolder;
	}
	
	/**
	 * Déplace le noeud dans le répertoire de destination. 
	 */
	public ClassificationBuilder moveNode() {
		if (! actualParentFolder.equals(destinationFolder)) {
			service.moveNode(node.getNodeReference(), destinationFolder);
			actualParentFolder = destinationFolder;
		}
		return this;
	}
	/**
	 * Copie le noeud dans le répertoire de destination. 
	 */
	public ClassificationBuilder copyNode() {
		if (! actualParentFolder.equals(destinationFolder)) {
			service.copyNode(node.getNodeReference(), destinationFolder);
		}
		return this;
	}
	/**
	 * Créer un lien secondaire dans le répertoire de destination.
	 */
	public ClassificationBuilder createLink() {
		service.createLink(node.getNodeReference(), destinationFolder);
		return this;
	}
	
}
