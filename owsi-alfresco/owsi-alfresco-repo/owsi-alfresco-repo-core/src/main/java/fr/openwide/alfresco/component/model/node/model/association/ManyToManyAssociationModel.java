package fr.openwide.alfresco.component.model.node.model.association;

import org.alfresco.service.namespace.QName;

public class ManyToManyAssociationModel extends AssociationModel {

	public ManyToManyAssociationModel(QName qName) {
		super(qName);
	}
	
	@Override
	public boolean isFromMany() {
		return true;
	}
	@Override
	public boolean isToMany() {
		return true;
	}

}
