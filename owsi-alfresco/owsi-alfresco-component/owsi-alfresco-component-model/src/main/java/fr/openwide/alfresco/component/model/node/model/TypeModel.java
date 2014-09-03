package fr.openwide.alfresco.component.model.node.model;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class TypeModel extends ContainerModel {

	public TypeModel(NameReference nameReference) {
		super(nameReference);
	}
	
	public TypeModel clone(NameReference newNameReference) {
		TypeModel clone = new TypeModel(newNameReference);
		copy(this, clone);
		return clone;
	}
	
}
