package fr.openwide.alfresco.component.model.node.model.property.single;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class BooleanPropertyModel extends SinglePropertyModel<Boolean> {

	public BooleanPropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	public Class<Boolean> getValueClass() {
		return Boolean.class;
	}
	
	@Override
	public String getDataType() {
		return "d:boolean";
	}
}
