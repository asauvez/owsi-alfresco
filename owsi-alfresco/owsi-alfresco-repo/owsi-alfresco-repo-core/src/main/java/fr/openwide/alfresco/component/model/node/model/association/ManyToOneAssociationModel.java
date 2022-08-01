package fr.openwide.alfresco.component.model.node.model.association;

import org.alfresco.service.namespace.QName;

public class ManyToOneAssociationModel extends AssociationModel {

	public ManyToOneAssociationModel(QName qName) {
		super(qName);
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
