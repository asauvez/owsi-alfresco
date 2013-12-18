package fr.openwide.alfresco.query.core.search.util;

import java.io.Serializable;

import fr.openwide.alfresco.query.core.node.model.AspectModel;
import fr.openwide.alfresco.query.core.node.model.TypeModel;
import fr.openwide.alfresco.query.core.node.model.property.PropertyModel;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.model.RepositoryPermission;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class RepositoryNodeBuilder {

	private RepositoryNode node = new RepositoryNode();

	public RepositoryNode getNode() {
		return node;
	}

	public RepositoryNodeBuilder nodeReference(NodeReference nodeReference) {
		node.setNodeReference(nodeReference);
		return this;
	}

	public RepositoryNodeBuilder type(TypeModel typeModel) {
		node.setType(typeModel.getNameReference());
		return this;
	}

	public <C extends Serializable> RepositoryNodeBuilder property(PropertyModel<?> propertyModel, C value) {
		node.getProperties().put(propertyModel.getNameReference(), value);
		return this;
	}

	public RepositoryNodeBuilder aspect(AspectModel aspectModel) {
		node.getAspects().add(aspectModel.getNameReference());
		return this;
	}

	public RepositoryNodeBuilder userPermission(RepositoryPermission permission) {
		node.getUserPermissions().add(permission);
		return this;
	}

	public RepositoryNodeBuilder primaryParent() {
		RepositoryNodeBuilder primaryParent = new RepositoryNodeBuilder();
		node.setPrimaryParent(primaryParent.node);
		return primaryParent;
	}
}
