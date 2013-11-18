package fr.openwide.alfresco.query.core.node.model.property;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.core.node.model.TypeModel;

public class LongPropertyModel extends PropertyModel<Long> {

	public LongPropertyModel(TypeModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Long> getValueClass() {
		return Long.class;
	}

}
