package fr.openwide.alfresco.component.model.node.model.property.single;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class EnumTextPropertyModel<E extends Enum<E>> extends TextPropertyModel {

	private final Class<E> enumClass;

	public EnumTextPropertyModel(ContainerModel type, NameReference nameReference, Class<E> enumClass) {
		super(type, nameReference);
		this.enumClass = enumClass;
	}

	public Class<E> getEnumClass() {
		return enumClass;
	}

}
