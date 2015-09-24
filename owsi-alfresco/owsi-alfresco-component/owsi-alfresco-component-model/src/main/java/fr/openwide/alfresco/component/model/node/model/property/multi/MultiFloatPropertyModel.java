package fr.openwide.alfresco.component.model.node.model.property.multi;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class MultiFloatPropertyModel extends AbstractMultiNumberPropertyModel<Float> {

	public MultiFloatPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Float> getValueClass() {
		return Float.class;
	}

}
