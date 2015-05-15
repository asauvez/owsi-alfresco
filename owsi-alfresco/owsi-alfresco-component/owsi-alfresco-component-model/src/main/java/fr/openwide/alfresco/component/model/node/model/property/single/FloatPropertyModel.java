package fr.openwide.alfresco.component.model.node.model.property.single;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class FloatPropertyModel extends SinglePropertyModel<Float> {

	public FloatPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Float> getValueClass() {
		return Float.class;
	}

}
