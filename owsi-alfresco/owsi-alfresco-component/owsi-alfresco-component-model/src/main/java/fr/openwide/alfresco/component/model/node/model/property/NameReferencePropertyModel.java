package fr.openwide.alfresco.component.model.node.model.property;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class NameReferencePropertyModel extends PropertyModel<NameReference> {

	public NameReferencePropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<NameReference> getValueClass() {
		return NameReference.class;
	}

}
