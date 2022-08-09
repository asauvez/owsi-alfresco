package fr.openwide.alfresco.component.model.node.model.property.single;

import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public class ChildAssociationRefPropertyModel extends SinglePropertyModel<ChildAssociationRef> {

	public ChildAssociationRefPropertyModel(ContainerModel type, QName qName) {
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
