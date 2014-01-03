package fr.openwide.alfresco.component.model.node.model.property.multi;

import java.io.Serializable;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public abstract class MultiPropertyModel<C extends Serializable> extends PropertyModel<C> {

	public MultiPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

}
