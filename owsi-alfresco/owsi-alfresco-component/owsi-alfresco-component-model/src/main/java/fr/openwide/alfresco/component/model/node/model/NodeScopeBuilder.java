package fr.openwide.alfresco.component.model.node.model;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.ContentPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.model.RepositoryPermission;

/**
 * Indique les données liés à un noeud à rapporter lors d'une recherche. 
 *  
 * @author asauvez
 */
public class NodeScopeBuilder {

	private NodeScope scope = new NodeScope();

	public NodeScope getScope() {
		return scope;
	}

	public NodeScopeBuilder fromNode(BusinessNode node) {
		RepositoryNode repositoryNode = node.getRepositoryNode();
		if (repositoryNode.getType() != null) type();
		if (node.getRepositoryNode().getPrimaryParentAssociation() != null) primaryParent();
		
		scope.getProperties().addAll(repositoryNode.getProperties().keySet());
		scope.getContentStrings().addAll(repositoryNode.getContentStrings().keySet());
		return this;
	}
	
	public NodeScopeBuilder nodeReference() {
		scope.setNodeReference(true);
		return this;
	}

	public NodeScopeBuilder path() {
		scope.setPath(true);
		return this;
	}

	public NodeScopeBuilder type() {
		scope.setType(true);
		return this;
	}

	public NodeScopeBuilder name() {
		return property(CmModel.object.name);
	}
	public NodeScopeBuilder property(PropertyModel<?> propertyModel) {
		scope.getProperties().add(propertyModel.getNameReference());
		return this;
	}
	public NodeScopeBuilder properties(ContainerModel type) {
		for (PropertyModel<?> property : type.getProperties()) {
			property(property);
		}
		return this;
	}

	public NodeScopeBuilder contentString() {
		return contentString(CmModel.content.content);
	}
	public NodeScopeBuilder contentString(ContentPropertyModel propertyModel) {
		scope.getContentStrings().add(propertyModel.getNameReference());
		return this;
	}

	public NodeScopeBuilder aspect(AspectModel aspectModel) {
		scope.getAspects().add(aspectModel.getNameReference());
		return this;
	}

	public NodeScopeBuilder userPermission(RepositoryPermission permission) {
		scope.getUserPermissions().add(permission);
		return this;
	}
	public NodeScopeBuilder accessPermissions() {
		scope.setAccessPermissions(true);
		return this;
	}
	

	public NodeScopeBuilder primaryParent() {
		NodeScopeBuilder primaryParent = new NodeScopeBuilder();
		scope.setPrimaryParent(primaryParent.scope);
		return primaryParent;
	}
	
	public NodeScopeBuilder childAssociationContains() {
		return childAssociation(CmModel.folder.contains);
	}
	public NodeScopeBuilder childAssociation(ChildAssociationModel childAssociation) {
		NodeScopeBuilder other = new NodeScopeBuilder();
		scope.getChildAssociations().put(childAssociation.getNameReference(), other.getScope());
		return other;
	}
	public NodeScopeBuilder parentAssociation(ChildAssociationModel childAssociation) {
		NodeScopeBuilder other = new NodeScopeBuilder();
		scope.getParentAssociations().put(childAssociation.getNameReference(), other.getScope());
		return other;
	}
	public NodeScopeBuilder targetAssociation(AssociationModel association) {
		NodeScopeBuilder other = new NodeScopeBuilder();
		scope.getTargetAssocs().put(association.getNameReference(), other.getScope());
		return other;
	}
	public NodeScopeBuilder sourceAssociation(AssociationModel association) {
		NodeScopeBuilder other = new NodeScopeBuilder();
		scope.getSourceAssocs().put(association.getNameReference(), other.getScope());
		return other;
	}

}
