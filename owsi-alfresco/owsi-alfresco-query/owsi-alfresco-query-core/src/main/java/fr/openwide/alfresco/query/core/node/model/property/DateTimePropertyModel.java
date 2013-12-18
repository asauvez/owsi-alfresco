package fr.openwide.alfresco.query.core.node.model.property;

import java.util.Date;

import fr.openwide.alfresco.query.core.node.model.ContainerModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class DateTimePropertyModel extends PropertyModel<Date> {

	public DateTimePropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Date> getValueClass() {
		return Date.class;
	}

}
