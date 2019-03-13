package fr.openwide.alfresco.repo.module.classification.model.builder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationEvent;
import fr.openwide.alfresco.repo.module.classification.service.impl.ClassificationServiceImpl;

/**
 * Utilitaire permettant de choisir le sous chemin de la classification.
 * 
 */
public class ClassificationWithRootBuilder extends AbstractClassificationBuilder<ClassificationWithRootBuilder> {

	private List<NodeReference> destinationFolders;

	public ClassificationWithRootBuilder(ClassificationServiceImpl service, ClassificationEvent event, List<NodeReference> destinationFolders) {
		super(service, event);
		this.destinationFolders = destinationFolders;
	}
	
	/**
	 * Créer un sous dossier par rapport au noeud en cours, si ce dossier n'existe pas encore.
	 */
	public ClassificationWithRootBuilder subFolder(String folderName) {
		return subFolder(new BusinessNode()
			.properties().name(folderName));
	}
	public ClassificationWithRootBuilder subFolder(String folderName, Consumer<BusinessNode> folderNodeConsumer) {
		BusinessNode node = new BusinessNode();
		folderNodeConsumer.accept(node);
		return subFolder(folderName, () -> node);
	}
	public ClassificationWithRootBuilder subFolder(String folderName, Supplier<BusinessNode> folderNodeSupplier) {
		destinationFolders = destinationFolders.stream()
				.map(destinationFolder -> service.subFolder(folderName, folderNodeSupplier, destinationFolder))
				.collect(Collectors.toList());
		return this;
	}
	/**
	 * @param folderNode Si le dossier n'existe pas encore, on va le créer avec le type, les propriétés et les permissions
	 * du noeud fourni.
	 */
	public ClassificationWithRootBuilder subFolder(BusinessNode folderNode) {
		return subFolder(
				folderNode.properties().getName(),
				() -> folderNode);
	}

	public ClassificationWithRootBuilder subFolder(SubFolderBuilder subFolderBuilder) {
		return subFolder(subFolderBuilder, () -> new BusinessNode());
	}
	public ClassificationWithRootBuilder subFolder(SubFolderBuilder subFolderBuilder, Supplier<BusinessNode> folderNodeSupplier) {
		Set<String> foldersName = subFolderBuilder.getFoldersName(this);
		return subFolders(foldersName, folderNodeSupplier);
	}
	public ClassificationWithRootBuilder subFolders(Collection<String> foldersName) {
		return subFolders(foldersName, () -> new BusinessNode());
	}
	public ClassificationWithRootBuilder subFolders(Collection<String> foldersName, Supplier<BusinessNode> folderNodeSupplier) {
		destinationFolders = destinationFolders.stream()
			.flatMap(destinationFolder -> 
				foldersName.stream()
					.map(folderName -> service.subFolder(folderName, folderNodeSupplier, destinationFolder)))
			.collect(Collectors.toList());
		return this;
	}

	public ClassificationWithRootBuilder forEachSubFolder() {
		return forEachSubFolder(nodeReference -> true);
	}
	public ClassificationWithRootBuilder forEachSubFolder(Predicate<NodeReference> predicate) {
		return new ClassificationWithRootBuilder(service, getEvent(), destinationFolders.stream()
				.flatMap(destinationFolder -> service.getNodeModelService().getChildrenAssocsContains(destinationFolder).stream())
				.filter(destinationFolder -> service.getNodeModelService().isType(destinationFolder, CmModel.folder))
				.filter(predicate)
				.collect(Collectors.toList()));
	}
	
	public ClassificationWithRootBuilder forEachTag() {
		List<NodeReference> tags = getProperty(CmModel.taggable.taggable); 
		if (tags == null) {
			tags = Collections.emptyList(); 
		}
		Set<String> foldersName = tags.stream()
				.map(tag -> service.getNodeModelService().getProperty(tag, CmModel.object.name))
				.collect(Collectors.toSet());
		return subFolders(foldersName);
	}
	
	/**
	 * Créer un sous dossier avec comme nom la valeur d'une propriété du noeud à classer.
	 */
	public ClassificationWithRootBuilder subFolderProperty(SinglePropertyModel<?> property) {
		return subFolder(new SubFolderBuilder(property));
	}
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
	
	public NodeReference getDestinationFolder() {
		if (destinationFolders.size() != 1) {
			throw new IllegalStateException(getDestinationFolders().toString());
		}
		return destinationFolders.iterator().next();
	}
	public Collection<NodeReference> getDestinationFolders() {
		return destinationFolders;
	}
	
	public ClassificationWithRootBuilder uniqueName() {
		if (destinationFolders.size() > 1) {
			throw new UnsupportedOperationException(destinationFolders.toString());
		}
		for (NodeReference destinationFolder : destinationFolders) {
			String newName = service.getNodeModelService().getUniqueChildName(destinationFolder, getNodeReference());
			name(newName);
		}
		return this;
	}
	
	public ClassificationWithRootBuilder name(String newName) {
		service.setNewName(getNodeReference(), newName);
		return this;
	}

	public ClassificationWithRootBuilder contentStore(String storeName) {
		service.setContentStore(getNodeReference(), storeName);
		return this;
	}
	/** N'indexe plus le noeud par ne pas surcharger Solr */
	public ClassificationWithRootBuilder index(boolean isIndexed) {
		service.setIndex(getNodeReference(), isIndexed);
		return this;
	}
	public ClassificationWithRootBuilder indexContent(boolean isContentIndexed) {
		service.setIndexContent(getNodeReference(), isContentIndexed);
		return this;
	}

	/**
	 * Déplace le noeud dans le répertoire de destination. 
	 */
	public ClassificationWithRootBuilder moveNode() {
		if (destinationFolders.size() > 1) {
			throw new UnsupportedOperationException("Can't move a node to more than one folder : " + destinationFolders);
		}
		for (NodeReference destinationFolder : destinationFolders) {
			service.moveNode(getNodeReference(), destinationFolder);
		}
		return this;
	}
	/**
	 * Copie le noeud dans le répertoire de destination. 
	 */
	public List<NodeReference> copyNode() {
		return destinationFolders.stream().map(destinationFolder -> 
			service.copyNode(getNodeReference(), destinationFolder, Optional.<String>empty()))
				.collect(Collectors.toList());
	}
	
	public ClassificationWithRootBuilder deletePrevious() {
		String currentName = service.getNodeModelService().getProperty(getNodeReference(), CmModel.object.name);
		for (NodeReference destinationFolder : destinationFolders) {
			service.deletePrevious(destinationFolder, currentName);
		}
		return this;
	}
	
	/**
	 * Créer un lien secondaire dans le répertoire de destination.
	 */
	public ClassificationWithRootBuilder createSecondaryParent() {
		for (NodeReference destinationFolder : destinationFolders) {
			service.createSecondaryParent(getNodeReference(), destinationFolder);
		}
		return this;
	}

	/**
	 * Créer un raccourci dans le répertoire de destination.
	 */
	public ClassificationWithRootBuilder createFileLink() {
		for (NodeReference destinationFolder : destinationFolders) {
			service.createFileLink(getNodeReference(), destinationFolder, Optional.empty());
		}
		return this;
	}
}
