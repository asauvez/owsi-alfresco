package fr.openwide.alfresco.repo.module.classification.model.builder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.alfresco.service.cmr.repository.NodeRef;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.StModel;
import fr.openwide.alfresco.component.model.repository.model.st.StSiteContainer.SiteContainerType;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repo.module.OwsiModel;
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
	public ClassificationWithRootBuilder rootFolder(NodeRef destinationFolder) {
		return rootFolders(Collections.singletonList(destinationFolder));
	}
	public ClassificationWithRootBuilder rootFolders(List<NodeRef> destinationFolders) {
		return new ClassificationWithRootBuilder(service, getEvent(), destinationFolders);
	}
	private Optional<ClassificationWithRootBuilder> rootFolder(Optional<NodeRef> destinationFolder) {
		return (destinationFolder.isPresent()) ? Optional.of(rootFolder(destinationFolder.get())) : Optional.empty();
	}
	/**
	 * Défini le noeud racine comme étant le résultat unique que renvoi la requête fournie.
	 */
	public Optional<ClassificationWithRootBuilder> rootFolder(RestrictionBuilder restrictionBuilder) {
		Optional<NodeRef> nodeRef = service.searchUniqueReference(restrictionBuilder);
		return rootFolder(nodeRef);
	}
	public ClassificationWithRootBuilder rootFolders(RestrictionBuilder restrictionBuilder) {
		return rootFolders(service.searchReference(restrictionBuilder));
	}
	
	public Optional<ClassificationWithRootBuilder> rootFolderPath(String path) {
		return rootFolder(new RestrictionBuilder()
				.path(path).of());
	}
	public ClassificationWithRootBuilder rootActualFolder() {
		NodeRef primaryParent = service.getNodeModelService().getPrimaryParent(getNodeRef()).get();
		return rootFolder(primaryParent);
	}
	public Optional<ClassificationWithRootBuilder> rootActualSite() {
		Optional<NodeRef> siteNode = service.getSiteNode(getNodeRef());
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
		Optional<NodeRef> optional = (cached) 
				? service.getByNamedPathCached(names)
				: service.getByNamedPath(names);
		return rootFolder(optional);
	}
	
	/**
	 * Recherche un noeud racine identifié par un owsi:identifiable. 
	 */
	public Optional<ClassificationWithRootBuilder> rootFolderIdentifier(NameReference identifier) {
		return rootFolder(new RestrictionBuilder()
				.eq(OwsiModel.identifiable.identifier, identifier).of());
	}
	public ClassificationWithRootBuilder rootFoldersIdentifier(NameReference identifier) {
		return rootFolders(new RestrictionBuilder()
				.eq(OwsiModel.identifiable.identifier, identifier).of());
	}

	public ClassificationWithRootBuilder rootFoldersWithAspect(AspectModel aspect) {
		return rootFolders(new RestrictionBuilder()
				.hasAspect(aspect).of());
	}

	/**
	 * Recherche un site du nom fourni. 
	 */
	public Optional<ClassificationWithRootBuilder> rootSite(String siteName) {
		return rootFolder(new RestrictionBuilder()
				.isType(StModel.site).of()
				.eq(StModel.site.name, siteName).of());
	}
	public Optional<ClassificationWithRootBuilder> rootSiteDocumentLibrary(String siteName) {
		return rootSite(siteName)
			.map(builder -> builder.subFolder(SiteContainerType.DOCUMENT_LIBRARY.getCode()));
	}

	/** 
	 * Défini le noeud racine de la classification comme étant le home folder de l'utilisateur en cours. 
	 */
	public Optional<ClassificationWithRootBuilder> rootHomeFolder() {
		Optional<NodeRef> homeFolder = service.getHomeFolder();
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
		service.deleteSecondaryParents(getNodeRef(), childAssociationModel);
		return this;
	}
	
	public void delete() {
		service.delete(getNodeRef(), false);
	}
	public void deletePermanently() {
		service.delete(getNodeRef(), true);
	}
}
