package fr.openwide.alfresco.component.model.node.model.property.single;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class TextPropertyModel extends SinglePropertyModel<String> {

	public TextPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<String> getValueClass() {
		return String.class;
	}

}
