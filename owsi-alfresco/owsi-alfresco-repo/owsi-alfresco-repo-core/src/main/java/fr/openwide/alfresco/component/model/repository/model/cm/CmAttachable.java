package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.association.ManyToManyAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmAttachable extends AspectModel {

	public CmAttachable() {
		super(CmModel.NAMESPACE.createQName("attachable"));
	}

	protected CmAttachable(QName qName) {
		super(qName);
	}
	
	public final ManyToManyAssociationModel attachments = new ManyToManyAssociationModel(CmModel.NAMESPACE.createQName("attachments"));
	
}
