package fr.openwide.alfresco.component.model.node.model.association;

import org.alfresco.service.namespace.QName;

public class OneToManyAssociationModel extends AssociationModel {

	public OneToManyAssociationModel(QName qName) {
		super(qName);
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
