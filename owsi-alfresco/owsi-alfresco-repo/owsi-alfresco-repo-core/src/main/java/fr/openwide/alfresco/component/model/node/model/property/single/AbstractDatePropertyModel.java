package fr.openwide.alfresco.component.model.node.model.property.single;

import java.util.Date;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public abstract class AbstractDatePropertyModel extends SinglePropertyModel<Date> {

	public AbstractDatePropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	public Class<Date> getValueClass() {
		return Date.class;
	}

}
