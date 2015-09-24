package fr.openwide.alfresco.component.model.node.model.property.multi;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public abstract class AbstractMultiNumberPropertyModel<C extends Number> extends MultiPropertyModel<C> {

	public AbstractMultiNumberPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

}
