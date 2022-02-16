package fr.openwide.alfresco.component.model.node.model.property.single;

import java.io.Serializable;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class AnyPropertyModel extends SinglePropertyModel<Serializable> {

	public AnyPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Serializable> getValueClass() {
		return Serializable.class;
	}
	
	@Override
	public String getDataType() {
		return "d:any";
	}
}
