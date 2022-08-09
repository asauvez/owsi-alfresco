package fr.openwide.alfresco.component.model.node.model.property.multi;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class MultiChildAssociationRefPropertyModel extends MultiPropertyModel<ChildAssociationRef> {

	public MultiChildAssociationRefPropertyModel(ContainerModel type, QName qName) {
		super(type, qName);
	}

	@Override
	public Class<ChildAssociationRef> getValueClass() {
		return ChildAssociationRef.class;
	}

	
	@Override
	public String getDataType() {
		return "d:childassocref";
	}
}
