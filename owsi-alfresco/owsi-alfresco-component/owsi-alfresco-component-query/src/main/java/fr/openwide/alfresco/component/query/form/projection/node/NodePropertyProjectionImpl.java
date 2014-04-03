package fr.openwide.alfresco.component.query.form.projection.node;

import java.io.Serializable;

import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class NodePropertyProjectionImpl<P extends Serializable> extends AbstractNodeProjectionImpl<P> {

	private final SinglePropertyModel<P> property;

	public NodePropertyProjectionImpl(NodeProjectionBuilder builder, SinglePropertyModel<P> property) {
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
	public void initNodeScope(NodeScope nodeScope) {
		super.initNodeScope(nodeScope);

		nodeScope.getProperties().add(property.getNameReference());
	}
}
