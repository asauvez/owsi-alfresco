package fr.openwide.alfresco.component.model.node.model.association;

import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class OneToManyAssociationModel extends AssociationModel {

	public OneToManyAssociationModel(NameReference nameReference) {
		super(nameReference);
	}
	
	@Override
	public boolean isFromMany() {
		return false;
	}
	@Override
	public boolean isToMany() {
		return true;
	}

}
