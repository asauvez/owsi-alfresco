package fr.openwide.alfresco.component.model.node.model.property.multi;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class MultiIntegerPropertyModel extends AbstractMultiNumberPropertyModel<Integer> {

	public MultiIntegerPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Integer> getValueClass() {
		return Integer.class;
	}
	
	@Override
	public String getDataType() {
		return "d:int";
	}

}
