package fr.openwide.alfresco.component.model.node.model.property.multi;

import java.util.Date;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class MultiDateTimePropertyModel extends MultiPropertyModel<Date> {

	public MultiDateTimePropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Date> getValueClass() {
		return Date.class;
	}

}
