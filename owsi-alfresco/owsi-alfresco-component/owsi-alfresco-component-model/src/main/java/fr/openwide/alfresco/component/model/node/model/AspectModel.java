package fr.openwide.alfresco.component.model.node.model;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class AspectModel extends ContainerModel {

	public AspectModel(NameReference nameReference) {
		super(nameReference);
	}

	public AspectModel clone(NameReference newNameReference) {
		AspectModel clone = new AspectModel(newNameReference);
		copy(this, clone);
		return clone;
	}
	
}
