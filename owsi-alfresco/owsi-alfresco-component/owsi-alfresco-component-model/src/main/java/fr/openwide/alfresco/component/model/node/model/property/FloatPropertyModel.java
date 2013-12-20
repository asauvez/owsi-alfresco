package fr.openwide.alfresco.component.model.node.model.property;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class FloatPropertyModel extends PropertyModel<Float> {

	public FloatPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Float> getValueClass() {
		return Float.class;
	}

}
