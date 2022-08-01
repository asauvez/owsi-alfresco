package fr.openwide.alfresco.component.model.node.model.property.multi;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class MultiIntegerPropertyModel extends AbstractMultiNumberPropertyModel<Integer> {

	public MultiIntegerPropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	public Class<Integer> getValueClass() {
		return Integer.class;
	}
	
	@Override
	public String getDataType() {
		return "d:int";
	}

}
