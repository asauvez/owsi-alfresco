package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmFolder extends CmObject {

	public CmFolder() {
		super(CmModel.NAMESPACE.createQName("folder"));
	}

	protected CmFolder(QName qName) {
		super(qName);
	}

	public final ChildAssociationModel contains = new ChildAssociationModel(CmModel.NAMESPACE.createQName("contains"));
}
