package fr.openwide.alfresco.query.core.node.model.property;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.core.node.model.TypeModel;

public class TextPropertyModel extends PropertyModel<String> {

	public TextPropertyModel(TypeModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<String> getValueClass() {
		return String.class;
	}

}
