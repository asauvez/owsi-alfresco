package fr.openwide.alfresco.query.core.node.model.property;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.core.node.model.TypeModel;

public class ContentPropertyModel extends PropertyModel<String> {

	public ContentPropertyModel(TypeModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<String> getValueClass() {
		return String.class;
	}

}
