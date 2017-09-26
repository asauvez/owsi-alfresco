package fr.openwide.alfresco.repo.module.classification.model.builder;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

	private NodeReference destinationFolder;

	public ClassificationWithRootBuilder(ClassificationServiceImpl service, ClassificationEvent event, NodeReference destinationFolder) {
		super(service, event);
		this.destinationFolder = destinationFolder;
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
		destinationFolder = service.subFolder(folderName, folderNodeSupplier, destinationFolder);
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
		return subFolder(subFolderBuilder, new BusinessNode());
	}
	public ClassificationWithRootBuilder subFolder(SubFolderBuilder subFolderBuilder, BusinessNode folderNode) {
		String folderName = subFolderBuilder.getFolderName(getNode());
		return subFolder(folderNode
				.properties().name(folderName));
	}

	/**
	 * Créer un sous dossier avec comme nom la valeur d'une propriété du noeud à classer.
	 */
	public ClassificationWithRootBuilder subFolderProperty(SinglePropertyModel<?> property) {
		return subFolder(new SubFolderBuilder(property));
	}
	public ClassificationWithRootBuilder subFolderProperty(SinglePropertyModel<?> property, BusinessNode folderNode) {
		return subFolder(new SubFolderBuilder(property), folderNode);
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
		return destinationFolder;
	}
	
	public ClassificationWithRootBuilder uniqueName() {
		String currentName = getNode().properties().getName();
		String newName = service.getUniqueName(destinationFolder, currentName);
		return name(newName);
	}
	
	public ClassificationWithRootBuilder name(String newName) {
		String currentName = getNode().properties().getName();
		if (! currentName.equals(newName)) {
			service.setNewName(getNodeReference(), newName);
			getNode().properties().name(newName);
		}
		return this;
	}
	
	/**
	 * Déplace le noeud dans le répertoire de destination. 
	 */
	public ClassificationWithRootBuilder moveNode() {
		service.moveNode(getNodeReference(), destinationFolder);
		return this;
	}
	/**
	 * Copie le noeud dans le répertoire de destination. 
	 */
	public NodeReference copyNode() {
		return service.copyNode(getNodeReference(), destinationFolder, Optional.<String>empty());
	}
	public ClassificationWithRootBuilder deletePrevious() {
		service.deletePrevious(destinationFolder, getNode().properties().getName());
		return this;
	}
	
	/**
	 * Créer un lien secondaire dans le répertoire de destination.
	 */
	public ClassificationWithRootBuilder createSecondaryParent() {
		service.createSecondaryParent(getNodeReference(), destinationFolder);
		return this;
	}

	/**
	 * Créer un raccourci dans le répertoire de destination.
	 */
	public ClassificationWithRootBuilder createFileLink() {
		service.createFileLink(getNodeReference(), destinationFolder, Optional.empty());
		return this;
	}
}
