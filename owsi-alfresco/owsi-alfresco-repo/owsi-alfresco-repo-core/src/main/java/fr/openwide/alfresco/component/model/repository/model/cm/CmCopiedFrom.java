package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.association.ManyToOneAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmCopiedFrom extends AspectModel {

	public CmCopiedFrom() {
		super(CmModel.NAMESPACE.createQName("copiedfrom"));
	}

	protected CmCopiedFrom(QName qName) {
		super(qName);
	}

	public final ManyToOneAssociationModel original = new ManyToOneAssociationModel(CmModel.NAMESPACE.createQName("original"));

}
