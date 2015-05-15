package fr.openwide.alfresco.component.model.node.model.property.single;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class IntegerPropertyModel extends SinglePropertyModel<Integer> {

	public IntegerPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Integer> getValueClass() {
		return Integer.class;
	}

}
