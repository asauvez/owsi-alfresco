package fr.openwide.alfresco.component.model.node.model.property.multi;

import java.util.Date;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public abstract class AbstractMultiDatePropertyModel extends MultiPropertyModel<Date> {

	public AbstractMultiDatePropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	public Class<Date> getValueClass() {
		return Date.class;
	}

}
