package fr.openwide.alfresco.component.model.node.model.property.multi;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class MultiFloatPropertyModel extends MultiPropertyModel<Float> {

	public MultiFloatPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Float> getValueClass() {
		return Float.class;
	}

}
