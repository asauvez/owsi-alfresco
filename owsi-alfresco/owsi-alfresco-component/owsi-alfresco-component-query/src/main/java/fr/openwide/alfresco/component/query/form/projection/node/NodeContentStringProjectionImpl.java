package fr.openwide.alfresco.component.query.form.projection.node;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class NodeContentStringProjectionImpl extends AbstractNodeProjectionImpl<String> {

	private final PropertyModel<RepositoryContentData> property;
	
	public NodeContentStringProjectionImpl(NodeProjectionBuilder builder, PropertyModel<RepositoryContentData> property) {
		super(builder, String.class);
		this.property = property;
	}

	@Override
	public String getDefaultLabelCode() {
		NameReference nameReference = property.getNameReference();
		return nameReference.getNamespace() + "_" + nameReference.getName();
	}
	
	@Override
	public String apply(RepositoryNode node) {
		return node.getContentStrings().get(property.getNameReference());
	}

	@Override
	public void initNodeScope(NodeScope nodeScope) {
		super.initNodeScope(nodeScope);

		nodeScope.getContentStrings().add(property.getNameReference());
	}
}
