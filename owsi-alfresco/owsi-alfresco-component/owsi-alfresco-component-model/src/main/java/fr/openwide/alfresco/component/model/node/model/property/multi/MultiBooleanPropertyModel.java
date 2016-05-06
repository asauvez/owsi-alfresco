package fr.openwide.alfresco.component.model.node.model.property.multi;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class MultiBooleanPropertyModel extends MultiPropertyModel<Boolean> {

	public MultiBooleanPropertyModel(ContainerModel type, NameReference nameReference) {
		super(type, nameReference);
	}

	@Override
	public Class<Boolean> getValueClass() {
		return Boolean.class;
	}
	
	@Override
	public String getDataType() {
		return "d:boolean";
	}
}
