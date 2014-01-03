package fr.openwide.alfresco.component.model.node.model.property.single;

import java.util.Date;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class DatePropertyModel extends SinglePropertyModel<Date> {

	public DatePropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Date> getValueClass() {
		return Date.class;
	}

}
