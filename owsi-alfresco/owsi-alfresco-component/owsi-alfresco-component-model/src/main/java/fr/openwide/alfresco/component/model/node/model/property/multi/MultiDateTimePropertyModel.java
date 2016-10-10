package fr.openwide.alfresco.component.model.node.model.property.multi;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class MultiDateTimePropertyModel extends AbstractMultiDatePropertyModel {

	public MultiDateTimePropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public String getDataType() {
		return "d:datetime";
	}
}
