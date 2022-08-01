package fr.openwide.alfresco.component.model.node.model.property.multi;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class MultiDatePropertyModel extends AbstractMultiDatePropertyModel {

	public MultiDatePropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}
	
	@Override
	public String getDataType() {
		return "d:date";
	}

}
