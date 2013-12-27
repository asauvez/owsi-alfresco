package fr.openwide.alfresco.component.model.node.model;

import fr.openwide.alfresco.component.model.node.model.property.ContentPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.model.RepositoryPermission;

/**
 * Indique les données liés à un noeud à rapporter lors d'une recherche. 
 *  
 * @author asauvez
 */
public class NodeFetchDetailsBuilder {

	private NodeFetchDetails details = new NodeFetchDetails();

	public NodeFetchDetails getDetails() {
		return details;
	}

	public NodeFetchDetailsBuilder fromNode(BusinessNode node) {
		RepositoryNode repositoryNode = node.getRepositoryNode();
		if (repositoryNode.getType() != null) type();
		if (node.getPrimaryParent() != null) primaryParent();
		
		details.getProperties().addAll(repositoryNode.getProperties().keySet());
		details.getContentStrings().addAll(repositoryNode.getContentStrings().keySet());
		return this;
	}
	
	public NodeFetchDetailsBuilder nodeReference() {
		details.setNodeReference(true);
		return this;
	}

	public NodeFetchDetailsBuilder type() {
		details.setType(true);
		return this;
	}

	public NodeFetchDetailsBuilder name() {
		return property(CmModel.object.name);
	}
	public NodeFetchDetailsBuilder property(PropertyModel<?> propertyModel) {
		details.getProperties().add(propertyModel.getNameReference());
		return this;
	}
	public NodeFetchDetailsBuilder properties(ContainerModel type) {
		for (PropertyModel<?> property : type.getProperties()) {
			property(property);
		}
		return this;
	}

	public NodeFetchDetailsBuilder contentString() {
		return contentString(CmModel.content.content);
	}
	public NodeFetchDetailsBuilder contentString(ContentPropertyModel propertyModel) {
		details.getContentStrings().add(propertyModel.getNameReference());
		return this;
	}

	public NodeFetchDetailsBuilder aspect(AspectModel aspectModel) {
		details.getAspects().add(aspectModel.getNameReference());
		return this;
	}

	public NodeFetchDetailsBuilder userPermission(RepositoryPermission permission) {
		details.getUserPermissions().add(permission);
		return this;
	}
	public NodeFetchDetailsBuilder accessPermissions() {
		details.setAccessPermissions(true);
		return this;
	}
	

	public NodeFetchDetailsBuilder primaryParent() {
		NodeFetchDetailsBuilder primaryParent = new NodeFetchDetailsBuilder();
		details.setPrimaryParent(primaryParent.details);
		return primaryParent;
	}
	
	public NodeFetchDetailsBuilder childAssociationContains() {
		return childAssociation(CmModel.folder.contains);
	}
	public NodeFetchDetailsBuilder childAssociation(ChildAssociationModel childAssociation) {
		NodeFetchDetailsBuilder other = new NodeFetchDetailsBuilder();
		details.getChildAssociations().put(childAssociation.getNameReference(), other.getDetails());
		return other;
	}
	public NodeFetchDetailsBuilder targetAssociation(AssociationModel association) {
		NodeFetchDetailsBuilder other = new NodeFetchDetailsBuilder();
		details.getTargetAssocs().put(association.getNameReference(), other.getDetails());
		return other;
	}
	public NodeFetchDetailsBuilder sourceAssociation(AssociationModel association) {
		NodeFetchDetailsBuilder other = new NodeFetchDetailsBuilder();
		details.getSourceAssocs().put(association.getNameReference(), other.getDetails());
		return other;
	}

}
