package fr.openwide.alfresco.query.core.search.util;

import fr.openwide.alfresco.query.core.node.model.AspectModel;
import fr.openwide.alfresco.query.core.node.model.property.PropertyModel;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryPermission;

public class NodeFetchDetailsBuilder {

	private NodeFetchDetails details = new NodeFetchDetails();

	public NodeFetchDetails getDetails() {
		return details;
	}

	public NodeFetchDetailsBuilder nodeReference() {
		details.setNodeReference(true);
		return this;
	}

	public NodeFetchDetailsBuilder type() {
		details.setType(true);
		return this;
	}

	public NodeFetchDetailsBuilder property(PropertyModel<?> propertyModel) {
		details.getProperties().add(propertyModel.getNameReference());
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

	public NodeFetchDetailsBuilder primaryParent() {
		NodeFetchDetailsBuilder primaryParent = new NodeFetchDetailsBuilder();
		details.setPrimaryParent(primaryParent.details);
		return primaryParent;
	}
}
