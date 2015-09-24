package fr.openwide.alfresco.component.query.form.projection.node;

import fr.openwide.alfresco.api.core.node.binding.content.serializer.StringRepositoryContentSerializer;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;

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
		return (String) node.getContents().get(property.getNameReference());
	}

	@Override
	public void initNodeScope(NodeScope nodeScope) {
		super.initNodeScope(nodeScope);

		nodeScope.getContentDeserializers().put(property.getNameReference(), StringRepositoryContentSerializer.INSTANCE);
	}
}
