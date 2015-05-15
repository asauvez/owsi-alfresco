package fr.openwide.alfresco.component.model.node.model.property.multi;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class MultiIntegerPropertyModel extends MultiPropertyModel<Integer> {

	public MultiIntegerPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Integer> getValueClass() {
		return Integer.class;
	}

}
