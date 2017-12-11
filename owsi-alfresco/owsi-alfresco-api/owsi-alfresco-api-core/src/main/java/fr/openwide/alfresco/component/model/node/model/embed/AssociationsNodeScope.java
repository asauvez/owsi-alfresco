package fr.openwide.alfresco.component.model.node.model.embed;

import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.model.association.AssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class AssociationsNodeScope {

	private final NodeScopeBuilder builder;
	private final NodeScope scope;
	
	public AssociationsNodeScope(NodeScopeBuilder builder) {
		this.builder = builder;
		this.scope = builder.getScope();
	}
	
	public NodeScopeBuilder primaryParent() {
		NodeScopeBuilder primaryParent = new NodeScopeBuilder();
		scope.setPrimaryParent(primaryParent.getScope());
		return primaryParent;
	}
	public NodeScopeBuilder recursivePrimaryParent() {
		scope.setRecursivePrimaryParent(true);
		return builder;
	}
	
	public NodeScopeBuilder rendition(NameReference renditionName) {
		NodeScopeBuilder other = new NodeScopeBuilder();
		scope.getRenditions().put(renditionName, other.getScope());
		return other;
	} 
	
	public NodeScopeBuilder childContains() {
		return child(CmModel.folder.contains);
	}
	public NodeScopeBuilder child(ChildAssociationModel childAssociation) {
		NodeScopeBuilder other = new NodeScopeBuilder();
		scope.getChildAssociations().put(childAssociation.getNameReference(), other.getScope());
		return other;
	}
	public NodeScopeBuilder parentContains() {
		return parent(CmModel.folder.contains);
	}
	public NodeScopeBuilder parent(ChildAssociationModel childAssociation) {
		NodeScopeBuilder other = new NodeScopeBuilder();
		scope.getParentAssociations().put(childAssociation.getNameReference(), other.getScope());
		return other;
	}
	public NodeScopeBuilder target(AssociationModel association) {
		NodeScopeBuilder other = new NodeScopeBuilder();
		scope.getTargetAssocs().put(association.getNameReference(), other.getScope());
		return other;
	}
	public NodeScopeBuilder source(AssociationModel association) {
		NodeScopeBuilder other = new NodeScopeBuilder();
		scope.getSourceAssocs().put(association.getNameReference(), other.getScope());
		return other;
	}

	public NodeScopeBuilder recursiveChildContains() {
		return recursiveChild(CmModel.folder.contains);
	}
	public NodeScopeBuilder recursiveChild(ChildAssociationModel childAssociation) {
		scope.getRecursiveChildAssociations().add(childAssociation.getNameReference());
		return builder;
	}
	public NodeScopeBuilder recursiveParentContains() {
		return recursiveParent(CmModel.folder.contains);
	}
	public NodeScopeBuilder recursiveParent(ChildAssociationModel childAssociation) {
		scope.getRecursiveParentAssociations().add(childAssociation.getNameReference());
		return builder;
	}

}
