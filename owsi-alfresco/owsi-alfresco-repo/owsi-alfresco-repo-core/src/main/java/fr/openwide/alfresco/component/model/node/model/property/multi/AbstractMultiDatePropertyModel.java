package fr.openwide.alfresco.component.model.node.model.property.multi;

import java.util.Date;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public abstract class AbstractMultiDatePropertyModel extends MultiPropertyModel<Date> {

	public AbstractMultiDatePropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Date> getValueClass() {
		return Date.class;
	}

}
