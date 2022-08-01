package fr.openwide.alfresco.component.model.node.model.property.single;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class IntegerPropertyModel extends AbstractNumberPropertyModel<Integer> {

	public IntegerPropertyModel(ContainerModel type, QName qName) {
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
