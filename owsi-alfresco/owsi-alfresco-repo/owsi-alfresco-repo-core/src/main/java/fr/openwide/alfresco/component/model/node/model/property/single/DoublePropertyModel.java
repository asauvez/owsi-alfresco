package fr.openwide.alfresco.component.model.node.model.property.single;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class DoublePropertyModel extends AbstractNumberPropertyModel<Double> {

	public DoublePropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Double> getValueClass() {
		return Double.class;
	}

	@Override
	public String getDataType() {
		return "d:double";
	}
}
