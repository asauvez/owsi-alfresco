package fr.openwide.alfresco.component.model.repository.model.rn;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.repository.model.RnModel;

public class RnRendition extends AspectModel {

	public RnRendition() {
		super(RnModel.NAMESPACE.createQName("rendition"));
	}

	protected RnRendition(QName qName) {
		super(qName);
	}

}
