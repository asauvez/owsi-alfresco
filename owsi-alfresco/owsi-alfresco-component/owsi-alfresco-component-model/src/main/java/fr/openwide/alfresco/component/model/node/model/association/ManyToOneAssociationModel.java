package fr.openwide.alfresco.component.model.node.model.association;

import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class ManyToOneAssociationModel extends AssociationModel {

	public ManyToOneAssociationModel(NameReference nameReference) {
		super(nameReference);
	}
	
	@Override
	public boolean isFromMany() {
		return true;
	}
	@Override
	public boolean isToMany() {
		return false;
	}

}
