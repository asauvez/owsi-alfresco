package fr.openwide.alfresco.repo.dictionary.classification.model;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repo.dictionary.classification.service.impl.ClassificationServiceImpl;
import fr.openwide.alfresco.repo.dictionary.model.OwsiModel;


public class ClassificationBuilder {

	private final ClassificationServiceImpl service;
	private final BusinessNode node;
	private final NodeReference originalParentFolder;
	private NodeReference destinationFolder;

	public ClassificationBuilder(ClassificationServiceImpl service, BusinessNode node) {
		this.service = service;
		this.node = node;
		this.originalParentFolder = node.assocs().primaryParent().getNodeReference();
		this.destinationFolder = originalParentFolder;
	}
	
	public ClassificationBuilder rootFolder(NodeReference destinationFolder) {
		this.destinationFolder = destinationFolder;
		return this;
	}
	public ClassificationBuilder rootFolder(RestrictionBuilder restrictionBuilder) {
		return rootFolder(restrictionBuilder, false);
	}
	
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
	public ClassificationBuilder rootFolderIdentifier(NameReference identifier) {
		return rootFolder(new RestrictionBuilder()
				.eq(OwsiModel.identifiable.identifier, identifier).of(), true);
	}
	
	public ClassificationBuilder subFolder(String folderName) {
		return subFolder(new BusinessNode()
			.properties().name(folderName));
	}
	public ClassificationBuilder subFolder(BusinessNode folderNode) {
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
	
	public NodeReference getDestinationFolder() {
		return destinationFolder;
	}
	
	public ClassificationBuilder moveNode() {
		if (! originalParentFolder.equals(destinationFolder)) {
			service.moveNode(node.getNodeReference(), destinationFolder);
		}
		return this;
	}
	public ClassificationBuilder copyNode() {
		if (! originalParentFolder.equals(destinationFolder)) {
			service.copyNode(node.getNodeReference(), destinationFolder);
		}
		return this;
	}
	public ClassificationBuilder createLink() {
		service.createLink(node.getNodeReference(), destinationFolder);
		return this;
	}
	
}
