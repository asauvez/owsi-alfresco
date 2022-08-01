package fr.openwide.alfresco.component.model.node.model.property.single;

import java.io.Serializable;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;

public abstract class SinglePropertyModel<C extends Serializable> extends PropertyModel<C> {

	public SinglePropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

}
