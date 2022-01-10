package fr.openwide.alfresco.repo.module.classification.model.builder;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.alfresco.service.cmr.repository.NodeRef;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.dictionary.node.service.impl.UniqueNameGenerator;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationEvent;
import fr.openwide.alfresco.repo.module.classification.service.impl.ClassificationServiceImpl;

/**
 * Utilitaire permettant de choisir le sous chemin de la classification.
 * 
 */
public class ClassificationWithRootBuilder extends AbstractClassificationBuilder<ClassificationWithRootBuilder> {

	private List<NodeRef> destinationFolders;

	public ClassificationWithRootBuilder(ClassificationServiceImpl service, ClassificationEvent event, List<NodeRef> destinationFolders) {
		super(service, event);
		this.destinationFolders = destinationFolders;
	}
	
	/**
	 * Créer un sous dossier par rapport au noeud en cours, si ce dossier n'existe pas encore.
	 */
	public ClassificationWithRootBuilder subFolder(String folderName) {
		destinationFolders = destinationFolders.stream()
				.map(destinationFolder -> service.subFolder(folderName, destinationFolder))
				.collect(Collectors.toList());
		return this;
	}
	/** @Deprecated @see #doWithDestinationFolder(Consumer) */
	public ClassificationWithRootBuilder subFolder(String folderName, Consumer<BusinessNode> folderNodeConsumer) {
		BusinessNode node = new BusinessNode();
		folderNodeConsumer.accept(node);
		return subFolder(folderName, () -> node);
	}
	/** @Deprecated @see #doWithDestinationFolder(Consumer) */
	@SuppressWarnings("deprecation")
	public ClassificationWithRootBuilder subFolder(String folderName, Supplier<BusinessNode> folderNodeSupplier) {
		destinationFolders = destinationFolders.stream()
				.map(destinationFolder -> service.subFolder(folderName, folderNodeSupplier, destinationFolder))
				.collect(Collectors.toList());
		return this;
	}
	
	public ClassificationWithRootBuilder subFolder(String folderName, 
			ContainerModel folderType, PropertyModel<?> ... properties) {
		subFolder(folderName);
		return addAspectToFolder(folderType, properties);
	}
	
	/**
	 * @param folderNode Si le dossier n'existe pas encore, on va le créer avec le type, les propriétés et les permissions
	 * du noeud fourni.
	 */
	/** @Deprecated @see #doWithDestinationFolder(Consumer) */
	public ClassificationWithRootBuilder subFolder(BusinessNode folderNode) {
		return subFolder(
				folderNode.properties().getName(),
				() -> folderNode);
	}

	public ClassificationWithRootBuilder subFolder(SubFolderBuilder subFolderBuilder) {
		Set<String> foldersName = subFolderBuilder.getFoldersName(this);
		return subFolders(foldersName);
	}
	/** @Deprecated @see #doWithDestinationFolder(Consumer) */
	public ClassificationWithRootBuilder subFolder(SubFolderBuilder subFolderBuilder, Supplier<BusinessNode> folderNodeSupplier) {
		Set<String> foldersName = subFolderBuilder.getFoldersName(this);
		return subFolders(foldersName, folderNodeSupplier);
	}
	
	public ClassificationWithRootBuilder subFolders(Collection<String> foldersName) {
		destinationFolders = destinationFolders.stream()
			.flatMap(destinationFolder -> 
				foldersName.stream()
					.map(folderName -> service.subFolder(folderName, destinationFolder)))
			.collect(Collectors.toList());
		return this;
	}
	/** @Deprecated @see #doWithDestinationFolder(Consumer) */
	@SuppressWarnings("deprecation")
	public ClassificationWithRootBuilder subFolders(Collection<String> foldersName, Supplier<BusinessNode> folderNodeSupplier) {
		destinationFolders = destinationFolders.stream()
			.flatMap(destinationFolder -> 
				foldersName.stream()
					.map(folderName -> service.subFolder(folderName, folderNodeSupplier, destinationFolder)))
			.collect(Collectors.toList());
		return this;
	}

	public ClassificationWithRootBuilder forEachSubFolder() {
		return forEachSubFolder(nodeRef -> true);
	}
	public ClassificationWithRootBuilder forEachSubFolder(Predicate<NodeRef> predicate) {
		return new ClassificationWithRootBuilder(service, getEvent(), destinationFolders.stream()
				.flatMap(destinationFolder -> service.getNodeModelService().getChildrenAssocsContains(destinationFolder).stream())
				.filter(destinationFolder -> service.getNodeModelService().isType(destinationFolder, CmModel.folder))
				.filter(predicate)
				.collect(Collectors.toList()));
	}
	
	public ClassificationWithRootBuilder forEachTag() {
		return subFolders(service.getTagsName(getNodeRef()));
	}
	
	/**
	 * Créer un sous dossier avec comme nom la valeur d'une propriété du noeud à classer.
	 */
	public ClassificationWithRootBuilder subFolderProperty(SinglePropertyModel<?> property) {
		return subFolder(new SubFolderBuilder(property));
	}
	/** @Deprecated @see #doWithDestinationFolder(Consumer) */
	public ClassificationWithRootBuilder subFolderProperty(SinglePropertyModel<?> property, Supplier<BusinessNode> folderNodeSupplier) {
		return subFolder(new SubFolderBuilder(property), folderNodeSupplier);
	}

	public ClassificationWithRootBuilder subFolderYear() {
		return subFolder(new SubFolderBuilder(CmModel.auditable.created)
			.formatYear());
	}
	public ClassificationWithRootBuilder subFolderMonth() {
		return subFolder(new SubFolderBuilder(CmModel.auditable.created)
			.formatMonth());
	}
	public ClassificationWithRootBuilder subFolderDay() {
		return subFolder(new SubFolderBuilder(CmModel.auditable.created)
			.formatDay());
	}
	public ClassificationWithRootBuilder subFolderHour() {
		return subFolder(new SubFolderBuilder(CmModel.auditable.created)
			.formatHour());
	}
	public ClassificationWithRootBuilder subFolderMinute() {
		return subFolder(new SubFolderBuilder(CmModel.auditable.created)
			.formatMinute());
	}
	public ClassificationWithRootBuilder subFolderDate(String pattern) {
		return subFolder(new SubFolderBuilder(CmModel.auditable.created)
			.formatDate(pattern));
	}
	
	public NodeRef getDestinationFolder() {
		if (destinationFolders.size() != 1) {
			throw new IllegalStateException(getDestinationFolders().toString());
		}
		return destinationFolders.iterator().next();
	}
	public List<NodeRef> getDestinationFolders() {
		return destinationFolders;
	}
	
	public ClassificationWithRootBuilder name(String newName) {
		service.setNewName(getNodeRef(), newName);
		return this;
	}

	public ClassificationWithRootBuilder contentStore(String storeName) {
		service.setContentStore(getNodeRef(), storeName);
		return this;
	}
	/** N'indexe plus le noeud par ne pas surcharger Solr */
	public ClassificationWithRootBuilder index(boolean isIndexed) {
		service.setIndex(getNodeRef(), isIndexed);
		return this;
	}
	public ClassificationWithRootBuilder indexContent(boolean isContentIndexed) {
		service.setIndexContent(getNodeRef(), isContentIndexed);
		return this;
	}

	/**
	 * Déplace le noeud dans le répertoire de destination. 
	 */
	public ClassificationWithRootBuilder moveNode() {
		if (destinationFolders.size() > 1) {
			throw new UnsupportedOperationException("Can't move a node to more than one folder : " + destinationFolders);
		}
		for (NodeRef destinationFolder : destinationFolders) {
			service.moveNode(getNodeRef(), destinationFolder);
		}
		return this;
	}
	
	public ClassificationWithRootBuilder moveWithUniqueName() {
		return renameAndMoveNode(getNodeName());
	}
	
	public ClassificationWithRootBuilder renameAndMoveNode(String newName) {
		if (destinationFolders.size() > 1) {
			throw new UnsupportedOperationException("Can't move a node to more than one folder : " + destinationFolders);
		}
		return doWithDestinationFolder(destinationFolder -> 
			service.renameAndMoveNode(getNodeRef(), destinationFolder, newName));
	}
	/**
	 * Copie le noeud dans le répertoire de destination. 
	 */
	public List<NodeRef> copyNode() {
		return destinationFolders.stream().map(destinationFolder -> 
			service.copyNode(getNodeRef(), destinationFolder, Optional.<String>empty()))
				.collect(Collectors.toList());
	}
	
	
	public ClassificationWithRootBuilder doWithDestinationFolder(Consumer<NodeRef> consumer) {
		for (NodeRef destinationFolder : destinationFolders) {
			consumer.accept(destinationFolder);
		}
		return this;
	}
	
	public ClassificationWithRootBuilder addAspectToFolder(ContainerModel folderType, PropertyModel<?> ... properties) {
		return doWithDestinationFolder(destinationFolder -> {
			NodeModelRepositoryService nodeModelService = service.getNodeModelService();
			
			if (folderType instanceof TypeModel) {
				nodeModelService.setType(destinationFolder, (TypeModel) folderType);
			} else if (folderType instanceof AspectModel) {
				nodeModelService.addAspect(destinationFolder, (AspectModel) folderType);
			} else {
				throw new IllegalStateException(folderType.toString());
			}
			for (PropertyModel<?> property : properties) {
				if (property instanceof SinglePropertyModel) {
					@SuppressWarnings("unchecked")
					SinglePropertyModel<Serializable> sp = (SinglePropertyModel<Serializable>) property;
					Serializable value = getProperty(sp);
					nodeModelService.setProperty(destinationFolder, sp, value);
				} else if (property instanceof MultiPropertyModel) {
					@SuppressWarnings("unchecked")
					MultiPropertyModel<Serializable> mp = (MultiPropertyModel<Serializable>) property;
					List<Serializable> value = getProperty(mp);
					nodeModelService.setProperty(destinationFolder, mp, value);
				} else {
					throw new IllegalStateException(property.toString());
				}
			}
		});
	}	
	public void delete() {
		service.getNodeModelService().deleteNode(getNodeRef());
	}
	
	public ClassificationWithRootBuilder deletePrevious() {
		String currentName = service.getNodeModelService().getProperty(getNodeRef(), CmModel.object.name);
		return doWithDestinationFolder(destinationFolder -> 
			service.deletePrevious(destinationFolder, currentName));
	}
	
	/**
	 * Créer un lien secondaire dans le répertoire de destination.
	 */
	public ClassificationWithRootBuilder createSecondaryParent() {
		return doWithDestinationFolder(destinationFolder -> 
			service.createSecondaryParent(getNodeRef(), destinationFolder));
	}

	/**
	 * Déplace le document dans la première destination et crée des parents secondaires pour les autres.
	 */
	public ClassificationWithRootBuilder moveFirstAndCreateSecondaryParents() {
		service.moveNode(getNodeRef(), destinationFolders.get(0));
		
		for (NodeRef destinationFolder : destinationFolders.subList(1, destinationFolders.size())) {
			service.createSecondaryParent(getNodeRef(), destinationFolder);
		}
		return this;
	}
	
	public ClassificationWithRootBuilder moveFirstUniqueNameAndCreateSecondaryParents() {
		return renameMoveFirstAndCreateSecondaryParents(getNodeName());
	}
	
	public ClassificationWithRootBuilder renameMoveFirstAndCreateSecondaryParents(String newName) {
		// Nom unique dans tout les dossiers de destination
		String newValidUniqueName = service.getUniqueName(getNodeRef(), Optional.of(newName), destinationFolders, new UniqueNameGenerator());
		
		service.renameAndMoveNode(getNodeRef(), destinationFolders.get(0), newValidUniqueName);
		
		for (NodeRef destinationFolder : destinationFolders.subList(1, destinationFolders.size())) {
			service.createSecondaryParent(getNodeRef(), destinationFolder);
		}
		return this;
	}

	/**
	 * Créer un raccourci dans le répertoire de destination.
	 */
	public ClassificationWithRootBuilder createFileLink() {
		return doWithDestinationFolder(destinationFolder -> 
			service.createFileLink(getNodeRef(), destinationFolder, Optional.empty()));
	}
	
	public ClassificationWithRootBuilder firstDestination() {
		List<NodeRef> first = (destinationFolders.isEmpty()) ? Collections.emptyList() : destinationFolders.subList(0, 1);
		return new ClassificationWithRootBuilder(service, getEvent(), first);
	}
	public ClassificationWithRootBuilder otherDestinations() {
		List<NodeRef> others = (destinationFolders.isEmpty()) ? Collections.emptyList() : destinationFolders.subList(1, destinationFolders.size());
		return new ClassificationWithRootBuilder(service, getEvent(), others);
	}
	
	private String getNodeName() {
		return service.getNodeModelService().getProperty(getNodeRef(), CmModel.object.name);
	}
}
