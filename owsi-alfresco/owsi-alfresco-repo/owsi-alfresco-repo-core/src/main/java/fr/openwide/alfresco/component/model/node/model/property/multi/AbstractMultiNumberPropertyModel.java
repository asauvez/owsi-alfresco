package fr.openwide.alfresco.component.model.node.model.property.multi;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public abstract class AbstractMultiNumberPropertyModel<C extends Number> extends MultiPropertyModel<C> {

	public AbstractMultiNumberPropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

}
