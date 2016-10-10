package fr.openwide.alfresco.component.model.node.model.property.multi;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class MultiNameReferencePropertyModel extends MultiPropertyModel<NameReference> {

	public MultiNameReferencePropertyModel(ContainerModel type, NameReference nameReference) {
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
