package fr.openwide.alfresco.component.model.node.model.property.multi;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class MultiNodeReferencePropertyModel extends MultiPropertyModel<NodeReference> {

	public MultiNodeReferencePropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<NodeReference> getValueClass() {
		return NodeReference.class;
	}

}
