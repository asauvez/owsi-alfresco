package fr.openwide.alfresco.component.model.node.model.association;

import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class OneToOneAssociationModel extends AssociationModel {

	public OneToOneAssociationModel(NameReference nameReference) {
		super(nameReference);
	}
	
	@Override
	public boolean isFromMany() {
		return false;
	}
	@Override
	public boolean isToMany() {
		return false;
	}

}
