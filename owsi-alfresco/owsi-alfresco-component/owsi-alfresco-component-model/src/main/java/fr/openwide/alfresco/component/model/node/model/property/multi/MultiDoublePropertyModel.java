package fr.openwide.alfresco.component.model.node.model.property.multi;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class MultiDoublePropertyModel extends AbstractMultiNumberPropertyModel<Double> {

	public MultiDoublePropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Double> getValueClass() {
		return Double.class;
	}

}
