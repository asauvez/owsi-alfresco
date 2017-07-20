package fr.openwide.alfresco.component.model.node.model;

import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class TypeModel extends ContainerModel {

	public TypeModel(NameReference nameReference) {
		super(nameReference);
	}

	public TypeModel(TypeModel typeModel, NameReference nameReference) {
		this(nameReference);
		copy(typeModel, this);
	}
	
	public String getXmlModel() throws Exception {
		return getXmlModel("type");
	}
}
