package fr.openwide.alfresco.repo.module.classification.model.builder;

import java.util.Optional;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.module.model.OwsiModel;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.StModel;
import fr.openwide.alfresco.component.model.repository.model.st.StSiteContainer.SiteContainerType;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationEvent;
import fr.openwide.alfresco.repo.module.classification.service.impl.ClassificationServiceImpl;

/**
 * Utilitaire permettant de choisir l'emplacement racine de la classification.
 */
public class ClassificationBuilder extends AbstractClassificationBuilder<ClassificationBuilder> {

	public ClassificationBuilder(ClassificationServiceImpl service, ClassificationEvent event) {
		super(service, event);
	}
	
	/** 
	 * Défini un noeud racine de la classification. 
	 * Si on ne défini pas de noeud racine, la classification se fait à partir de l'emplacement actuel.
	 */
	public ClassificationWithRootBuilder rootFolder(NodeReference destinationFolder) {
		return new ClassificationWithRootBuilder(service, getEvent(), destinationFolder);
	}
	private Optional<ClassificationWithRootBuilder> rootFolder(Optional<NodeReference> destinationFolder) {
		return (destinationFolder.isPresent()) ? Optional.of(rootFolder(destinationFolder.get())) : Optional.empty();
	}
	/**
	 * Défini le noeud racine comme étant le résultat unique que renvoi la requête fournie.
	 */
	public Optional<ClassificationWithRootBuilder> rootFolder(RestrictionBuilder restrictionBuilder) {
		return rootFolder(restrictionBuilder, false);
	}
	
	/**
	 * @param cached Si vrai, alors mets en cache mémoire le résultat de la recherche.
	 */
	public Optional<ClassificationWithRootBuilder> rootFolder(RestrictionBuilder restrictionBuilder, boolean cached) {
		Optional<NodeReference> optional = (cached) 
				? service.searchUniqueReferenceCached(restrictionBuilder) 
				: service.searchUniqueReference(restrictionBuilder);
		return rootFolder(optional);
	}
	
	public Optional<ClassificationWithRootBuilder> rootFolderPath(String path) {
		return rootFolder(new RestrictionBuilder()
				.path(path).of(), true);
	}
	public ClassificationWithRootBuilder rootActualFolder() {
		return rootFolder(getNode().assocs().primaryParent().getNodeReference());
	}
	public Optional<ClassificationWithRootBuilder> rootActualSite() {
		Optional<NodeReference> siteNode = service.getSiteNode(getNodeReference());
		return (siteNode.isPresent()) ? Optional.of(rootFolder(siteNode.get())) : Optional.empty();
	}

	/**
	 * Recherche un noeud racine à partir d'un chemin. Le chemin est défini en dessous de "Company Home",
	 * avec une liste de cm:name. 
	 */
	public Optional<ClassificationWithRootBuilder> rootFolderNamedPath(String ... names) {
		return rootFolderNamedPath(true, names);
	}
	public Optional<ClassificationWithRootBuilder> rootFolderNamedPath(boolean cached, String ... names) {
		Optional<NodeReference> optional = (cached) 
				? service.getByNamedPathCached(names)
				: service.getByNamedPath(names);
		return rootFolder(optional);
	}
	
	/**
	 * Recherche un noeud racine identifié par un owsi:identifiable. 
	 */
	public Optional<ClassificationWithRootBuilder> rootFolderIdentifier(NameReference identifier) {
		return rootFolder(new RestrictionBuilder()
				.eq(OwsiModel.identifiable.identifier, identifier).of(), true);
	}

	/**
	 * Recherche un site du nom fourni. 
	 */
	public Optional<ClassificationWithRootBuilder> rootSite(String siteName) {
		return rootFolder(new RestrictionBuilder()
				.isType(StModel.site).of()
				.eq(StModel.site.name, siteName).of(), true);
	}
	public Optional<ClassificationWithRootBuilder> rootSiteDocumentLibrary(String siteName) {
		return rootSite(siteName)
			.map(builder -> builder.subFolder(SiteContainerType.DOCUMENT_LIBRARY.getCode()));
	}

	/** 
	 * Défini le noeud racine de la classification comme étant le home folder de l'utilisateur en cours. 
	 */
	public Optional<ClassificationWithRootBuilder> rootHomeFolder() {
		Optional<NodeReference> homeFolder = service.getHomeFolder();
		return rootFolder(homeFolder);
	}

	/** 
	 * Défini le noeud racine de la classification comme étant Company home. 
	 */
	public ClassificationWithRootBuilder rootCompanyHome() {
		return rootFolder(service.getCompanyHome());
	}

	public ClassificationBuilder unlinkSecondaryParents() {
		return unlinkSecondaryParents(CmModel.folder.contains);
	}
	public ClassificationBuilder unlinkSecondaryParents(ChildAssociationModel childAssociationModel) {
		service.deleteSecondaryParents(getNodeReference(), childAssociationModel);
		return this;
	}
	
	public void delete() {
		service.delete(getNodeReference(), false);
	}
	public void deletePermanently() {
		service.delete(getNodeReference(), true);
	}
}
