package fr.openwide.alfresco.query.core.node.model.property;

import fr.openwide.alfresco.query.core.node.model.ContainerModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class BooleanPropertyModel extends PropertyModel<Boolean> {

	public BooleanPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Boolean> getValueClass() {
		return Boolean.class;
	}

}
