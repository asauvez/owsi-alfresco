package fr.openwide.alfresco.query.core.node.model.property;

import fr.openwide.alfresco.query.core.node.model.TypeModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class TextPropertyModel extends PropertyModel<String> {

	public TextPropertyModel(TypeModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<String> getValueClass() {
		return String.class;
	}

}
