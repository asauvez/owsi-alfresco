package fr.openwide.alfresco.component.query.form.projection.node;

import java.io.Serializable;
import java.util.List;

import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class NodeMultiPropertyProjectionImpl<P extends Serializable> extends AbstractNodeProjectionImpl<List<P>> {

	private final MultiPropertyModel<P> property;

	public NodeMultiPropertyProjectionImpl(NodeProjectionBuilder builder, MultiPropertyModel<P> property) {
		super(builder, List.class);
		this.property = property;
	}

	@Override
	public String getDefaultLabelCode() {
		NameReference nameReference = property.getNameReference();
		return nameReference.getNamespace() + "_" + nameReference.getName();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<P> apply(RepositoryNode node) {
		return (List<P>) node.getProperty(property.getNameReference());
	}

	@Override
	public void initNodeScope(NodeScope nodeScope) {
		super.initNodeScope(nodeScope);

		nodeScope.getProperties().add(property.getNameReference());
	}
}
