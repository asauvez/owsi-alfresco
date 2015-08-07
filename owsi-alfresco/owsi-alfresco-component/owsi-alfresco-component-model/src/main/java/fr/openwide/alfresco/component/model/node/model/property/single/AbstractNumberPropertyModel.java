package fr.openwide.alfresco.component.model.node.model.property.single;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public abstract class AbstractNumberPropertyModel<C extends Number> extends SinglePropertyModel<C> {

	public AbstractNumberPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

}
