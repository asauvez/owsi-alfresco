package fr.openwide.alfresco.component.model.node.model.property.single;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class NameReferencePropertyModel extends SinglePropertyModel<NameReference> {

	public NameReferencePropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<NameReference> getValueClass() {
		return NameReference.class;
	}

	@Override
	public String getDataType() {
		return "d:text";
	}
}
