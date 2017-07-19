package fr.openwide.alfresco.repo.module.classification.model;

import java.util.function.Supplier;

import java.util.Optional;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repo.module.classification.service.impl.ClassificationServiceImpl;

/**
 * Utilitaire permettant de choisir le sous chemin de la classification.
 * 
 */
public class ClassificationWithRootBuilder {

	private final ClassificationServiceImpl service;
	private final BusinessNode node;
	private final NodeReference destinationFolder;

	public ClassificationWithRootBuilder(ClassificationServiceImpl service, BusinessNode node, NodeReference destinationFolder) {
		this.service = service;
		this.node = node;
		this.destinationFolder = destinationFolder;
	}
	
	/**
	 * Créer un sous dossier par rapport au noeud en cours, si ce dossier n'existe pas encore.
	 */
	public ClassificationWithRootBuilder subFolder(String folderName) {
		return subFolder(new BusinessNode()
			.properties().name(folderName));
	}
	public ClassificationWithRootBuilder subFolder(String folderName, Supplier<BusinessNode> folderNodeSupplier) {
		NodeReference newDestinationFolder = service.subFolder(folderName, folderNodeSupplier, destinationFolder);
		return new ClassificationWithRootBuilder(service, node, newDestinationFolder);
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
		String folderName = subFolderBuilder.getFolderName(node);
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
	
	public NodeReference getDestinationFolder() {
		return destinationFolder;
	}
	
	public ClassificationWithRootBuilder uniqueName() {
		String currentName = node.properties().getName();
		String newName = service.getUniqueName(destinationFolder, currentName);
		return name(newName);
	}
	
	public ClassificationWithRootBuilder name(String newName) {
		String currentName = node.properties().getName();
		if (! currentName.equals(newName)) {
			service.setNewName(node.getNodeReference(), newName);
			node.properties().name(newName);
		}
		return this;
	}
	
	/**
	 * Déplace le noeud dans le répertoire de destination. 
	 */
	public ClassificationWithRootBuilder moveNode() {
		service.moveNode(node.getNodeReference(), destinationFolder);
		return this;
	}
	/**
	 * Copie le noeud dans le répertoire de destination. 
	 */
	public NodeReference copyNode() {
		return service.copyNode(node.getNodeReference(), destinationFolder, Optional.<String>empty());
	}
	public ClassificationWithRootBuilder deletePrevious() {
		service.deletePrevious(destinationFolder, node.properties().getName());
		return this;
	}
	
	/**
	 * Créer un lien secondaire dans le répertoire de destination.
	 */
	public ClassificationWithRootBuilder createSecondaryParent() {
		service.createSecondaryParent(node.getNodeReference(), destinationFolder);
		return this;
	}

	/**
	 * Créer un raccourci dans le répertoire de destination.
	 */
	public ClassificationWithRootBuilder createFileLink() {
		service.createFileLink(node.getNodeReference(), destinationFolder, Optional.empty());
		return this;
	}
}
