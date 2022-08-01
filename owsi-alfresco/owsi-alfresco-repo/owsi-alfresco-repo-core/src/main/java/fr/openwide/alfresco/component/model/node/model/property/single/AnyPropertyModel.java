package fr.openwide.alfresco.component.model.node.model.property.single;

import java.io.Serializable;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class AnyPropertyModel extends SinglePropertyModel<Serializable> {

	public AnyPropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	public Class<Serializable> getValueClass() {
		return Serializable.class;
	}
	
	@Override
	public String getDataType() {
		return "d:any";
	}
}
