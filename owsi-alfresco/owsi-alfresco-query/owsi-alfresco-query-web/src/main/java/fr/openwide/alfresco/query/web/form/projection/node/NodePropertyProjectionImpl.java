package fr.openwide.alfresco.query.web.form.projection.node;

import java.io.Serializable;

import fr.openwide.alfresco.query.core.node.model.property.PropertyModel;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class NodePropertyProjectionImpl<P extends Serializable> extends NodeProjectionImpl<P> {

	private final PropertyModel<P> property;

	public NodePropertyProjectionImpl(NodeProjectionBuilder builder, PropertyModel<P> property) {
		super(builder, property.getValueClass());
		this.property = property;
	}

	@Override
	public String getDefaultLabelCode() {
		NameReference nameReference = property.getNameReference();
		return nameReference.getNamespace() + "_" + nameReference.getName();
	}

	@Override
	@SuppressWarnings("unchecked")
	public P apply(RepositoryNode node) {
		return (P) node.getProperties().get(property.getNameReference());
	}

	@Override
	public void initNodeFetchDetails(NodeFetchDetails nodeFetchDetails) {
		super.initNodeFetchDetails(nodeFetchDetails);

		nodeFetchDetails.getProperties().add(property.getNameReference());
	}
}
