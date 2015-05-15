package fr.openwide.alfresco.component.model.node.model.property.single;

import java.util.Date;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class DateTimePropertyModel extends SinglePropertyModel<Date> {

	public DateTimePropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Date> getValueClass() {
		return Date.class;
	}

}
