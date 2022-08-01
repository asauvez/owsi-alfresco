package fr.openwide.alfresco.component.model.node.model.property.multi;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class MultiQNamePropertyModel extends MultiPropertyModel<QName> {

	public MultiQNamePropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	public Class<QName> getValueClass() {
		return QName.class;
	}

	@Override
	public String getDataType() {
		return "d:text";
	}
}
