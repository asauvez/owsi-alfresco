package fr.openwide.alfresco.component.model.node.model.property.single;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class TextPropertyModel extends SinglePropertyModel<String> {

	public TextPropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	public Class<String> getValueClass() {
		return String.class;
	}

	@Override
	public String getDataType() {
		return "d:text";
	}

}
