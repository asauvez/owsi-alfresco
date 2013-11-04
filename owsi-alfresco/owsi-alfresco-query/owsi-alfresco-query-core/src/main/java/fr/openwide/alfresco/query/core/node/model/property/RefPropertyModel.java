package fr.openwide.alfresco.query.core.node.model.property;

import fr.openwide.alfresco.query.core.node.model.TypeModel;
import fr.openwide.alfresco.query.core.node.model.value.NameReference;
import fr.openwide.alfresco.query.core.node.model.value.NodeReference;

public class RefPropertyModel extends PropertyModel<NodeReference> {

	public RefPropertyModel(TypeModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<NodeReference> getValueClass() {
		return NodeReference.class;
	}

}
