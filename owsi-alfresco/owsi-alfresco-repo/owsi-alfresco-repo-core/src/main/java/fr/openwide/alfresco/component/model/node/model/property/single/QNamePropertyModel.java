package fr.openwide.alfresco.component.model.node.model.property.single;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class QNamePropertyModel extends SinglePropertyModel<QName> {

	public QNamePropertyModel(ContainerModel type, QName qName) {
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
