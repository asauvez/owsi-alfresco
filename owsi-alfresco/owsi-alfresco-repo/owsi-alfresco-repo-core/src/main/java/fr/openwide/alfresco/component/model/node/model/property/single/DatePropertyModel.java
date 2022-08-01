package fr.openwide.alfresco.component.model.node.model.property.single;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class DatePropertyModel extends AbstractDatePropertyModel {

	public DatePropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	public String getDataType() {
		return "d:date";
	}
}
