package fr.openwide.alfresco.component.model.node.model.property.multi;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class MultiLongPropertyModel extends AbstractMultiNumberPropertyModel<Long> {

	public MultiLongPropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	public Class<Long> getValueClass() {
		return Long.class;
	}

	@Override
	public String getDataType() {
		return "d:long";
	}
}
