package fr.openwide.alfresco.query.core.search.util;

import java.io.Serializable;

import fr.openwide.alfresco.query.core.node.model.AspectModel;
import fr.openwide.alfresco.query.core.node.model.TypeModel;
import fr.openwide.alfresco.query.core.node.model.property.PropertyModel;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.model.RepositoryPermission;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class RepositoryNodeWrapper {

	private RepositoryNode node;

	public RepositoryNodeWrapper(RepositoryNode node) {
		this.node = node;
	}

	public RepositoryNode getNode() {
		return node;
	}

	public NodeReference getNodeReference() {
		return node.getNodeReference();
	}
	
	public boolean isType(TypeModel typeModel) {
		return typeModel.getNameReference().equals(node.getType());
	}

	public boolean hasAspect(AspectModel aspectModel) {
		return node.getAspects().contains(aspectModel.getNameReference());
	}

	@SuppressWarnings("unchecked")
	public <C extends Serializable> C getProperty(PropertyModel<C> propertyModel) {
		return (C) node.getProperties().get(propertyModel.getNameReference());
	}

	public boolean hasUserPermission(RepositoryPermission permission) {
		return node.getUserPermissions().contains(permission);
	}

	public RepositoryNodeWrapper getPrimaryParent() {
		return new RepositoryNodeWrapper(node.getPrimaryParent());
	}
}
