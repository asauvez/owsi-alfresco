package fr.openwide.alfresco.component.model.repository.model.rn;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.RnModel;

public class RnRenditioned extends AspectModel {

	public RnRenditioned() {
		super(RnModel.NAMESPACE.createQName("renditioned"));
	}

	protected RnRenditioned(QName qName) {
		super(qName);
	}

	public final ChildAssociationModel rendition = new ChildAssociationModel(RnModel.NAMESPACE.createQName("rendition"));
}
