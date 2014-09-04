package fr.openwide.alfresco.component.model.node.model;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class AspectModel extends ContainerModel {

	public AspectModel(NameReference nameReference) {
		super(nameReference);
	}

	public AspectModel(AspectModel aspectModel, NameReference nameReference) {
		this(nameReference);
		copy(aspectModel, this);
	}
	
}
