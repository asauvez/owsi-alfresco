package fr.openwide.alfresco.component.model.node.model.property.single;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class LongPropertyModel extends SinglePropertyModel<Long> {

	public LongPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Long> getValueClass() {
		return Long.class;
	}

}
