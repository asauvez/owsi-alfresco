package fr.openwide.alfresco.component.model.node.model.property.single;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class DateTimePropertyModel extends AbstractDatePropertyModel {

	public DateTimePropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	public String getDataType() {
		return "d:datetime";
	}
}
