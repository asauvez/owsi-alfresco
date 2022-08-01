package fr.openwide.alfresco.component.model.node.model.property.single;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class EnumTextPropertyModel<E extends Enum<E>> extends TextPropertyModel {

	private final Class<E> enumClass;

	public EnumTextPropertyModel(ContainerModel type, QName qName, Class<E> enumClass) {
		super(type, qName);
		this.enumClass = enumClass;
	}

	public Class<E> getEnumClass() {
		return enumClass;
	}

}
