package fr.openwide.alfresco.component.model.node.model.property.single;

import java.util.Date;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public abstract class AbstractDatePropertyModel extends SinglePropertyModel<Date> {

	public AbstractDatePropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Date> getValueClass() {
		return Date.class;
	}

}
