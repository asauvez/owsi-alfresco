package fr.openwide.alfresco.query.core.node.model.property;

import java.util.Date;

import fr.openwide.alfresco.query.core.node.model.TypeModel;
import fr.openwide.alfresco.query.core.node.model.value.NameReference;

public class DateTimePropertyModel extends PropertyModel<Date> {

	public DateTimePropertyModel(TypeModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Date> getValueClass() {
		return Date.class;
	}

}
