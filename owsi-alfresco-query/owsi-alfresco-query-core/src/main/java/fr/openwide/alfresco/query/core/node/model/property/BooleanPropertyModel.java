package fr.openwide.alfresco.query.core.node.model.property;

import fr.openwide.alfresco.query.core.node.model.TypeModel;
import fr.openwide.alfresco.query.core.node.model.value.NameReference;

public class BooleanPropertyModel extends PropertyModel<Boolean> {

	public BooleanPropertyModel(TypeModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Boolean> getValueClass() {
		return Boolean.class;
	}

}
