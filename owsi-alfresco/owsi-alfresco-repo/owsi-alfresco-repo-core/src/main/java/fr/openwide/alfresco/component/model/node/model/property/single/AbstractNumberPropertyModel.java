package fr.openwide.alfresco.component.model.node.model.property.single;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public abstract class AbstractNumberPropertyModel<C extends Number> extends SinglePropertyModel<C> {

	public AbstractNumberPropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

}
