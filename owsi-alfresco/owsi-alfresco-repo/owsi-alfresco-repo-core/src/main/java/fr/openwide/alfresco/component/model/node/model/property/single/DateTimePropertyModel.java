package fr.openwide.alfresco.component.model.node.model.property.single;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class DateTimePropertyModel extends AbstractDatePropertyModel {

	public DateTimePropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public String getDataType() {
		return "d:datetime";
	}
}
