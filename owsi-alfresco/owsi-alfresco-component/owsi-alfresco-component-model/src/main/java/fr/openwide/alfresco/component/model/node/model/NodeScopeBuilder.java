package fr.openwide.alfresco.component.model.node.model;

import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.embed.AssociationsNodeScope;
import fr.openwide.alfresco.component.model.node.model.embed.ContentsNodeScope;
import fr.openwide.alfresco.component.model.node.model.embed.PermissionsNodeScope;
import fr.openwide.alfresco.component.model.node.model.embed.PropertiesNodeScope;

public class NodeScopeBuilder {

	private NodeScope scope = new NodeScope();
	private PropertiesNodeScope propertiesNodeScope = new PropertiesNodeScope(this);
	private AssociationsNodeScope associationsNodeScope = new AssociationsNodeScope(this);
	private ContentsNodeScope contentsNodeScope = new ContentsNodeScope(this);
	private PermissionsNodeScope permissionsNodeScope = new PermissionsNodeScope(this);

	public NodeScope getScope() {
		return scope;
	}

	public NodeScopeBuilder fromNode(BusinessNode node) {
		RepositoryNode repositoryNode = node.getRepositoryNode();
		if (repositoryNode.getType() != null) type();
		if (node.getRepositoryNode().getPrimaryParentAssociation() != null) assocs().primaryParent();
		
		scope.getProperties().addAll(repositoryNode.getProperties().keySet());
		
		for (NameReference contentProperty : repositoryNode.getContents().keySet()) {
			// Le nodeScope sera envoyé à Alfresco sans le deserializer. Donc on peut mettre null.
			scope.getContentDeserializers().put(contentProperty, null);
		}
		
		if (! repositoryNode.getAccessControlList().isEmpty()) {
			scope.setAccessPermissions(true);
		}
		
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

	public PropertiesNodeScope properties() {
		return propertiesNodeScope;
	}

	public AssociationsNodeScope assocs() {
		return associationsNodeScope;
	}

	public ContentsNodeScope contents() {
		return contentsNodeScope;
	}

	public PermissionsNodeScope permissions() {
		return permissionsNodeScope;
	}

	public NodeScopeBuilder aspect(AspectModel aspectModel) {
		scope.getAspects().add(aspectModel.getNameReference());
		return this;
	}

}
