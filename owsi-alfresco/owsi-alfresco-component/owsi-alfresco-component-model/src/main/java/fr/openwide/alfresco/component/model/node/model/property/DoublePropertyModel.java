package fr.openwide.alfresco.component.model.node.model.property;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class DoublePropertyModel extends PropertyModel<Double> {

	public DoublePropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Double> getValueClass() {
		return Double.class;
	}

}
