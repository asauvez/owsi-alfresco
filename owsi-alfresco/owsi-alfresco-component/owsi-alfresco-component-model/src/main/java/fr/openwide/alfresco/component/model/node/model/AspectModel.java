package fr.openwide.alfresco.component.model.node.model;

import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class AspectModel extends ContainerModel {
	
	

	public AspectModel(NameReference nameReference) {
		super(nameReference);
	}

	public AspectModel(AspectModel aspectModel, NameReference nameReference) {
		this(nameReference);
		copy(aspectModel, this);
	}
	
	public String getXmlModel() throws Exception {
		return getXmlModel("aspect");
	}
	
	
}
