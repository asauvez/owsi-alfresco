package fr.openwide.alfresco.query.core.node.model.property;

import fr.openwide.alfresco.query.core.node.model.TypeModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class LongPropertyModel extends PropertyModel<Long> {

	public LongPropertyModel(TypeModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Long> getValueClass() {
		return Long.class;
	}

}
