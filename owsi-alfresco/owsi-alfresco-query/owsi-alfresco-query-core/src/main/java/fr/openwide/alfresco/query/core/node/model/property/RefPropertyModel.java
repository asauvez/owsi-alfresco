package fr.openwide.alfresco.query.core.node.model.property;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.api.node.model.NodeReference;
import fr.openwide.alfresco.query.core.node.model.TypeModel;

public class RefPropertyModel extends PropertyModel<NodeReference> {

	public RefPropertyModel(TypeModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<NodeReference> getValueClass() {
		return NodeReference.class;
	}

}
