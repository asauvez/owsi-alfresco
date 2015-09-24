package fr.openwide.alfresco.component.model.node.model.property.single;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class BooleanPropertyModel extends SinglePropertyModel<Boolean> {

	public BooleanPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Boolean> getValueClass() {
		return Boolean.class;
	}

}
