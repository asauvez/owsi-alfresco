package fr.openwide.alfresco.component.model.node.model.property.multi;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class MultiAssociationRefPropertyModel extends MultiPropertyModel<AssociationRef> {

	public MultiAssociationRefPropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	public Class<AssociationRef> getValueClass() {
		return AssociationRef.class;
	}

	
	@Override
	public String getDataType() {
		return "d:assocref";
	}
}
