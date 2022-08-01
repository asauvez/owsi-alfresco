package fr.openwide.alfresco.component.model.repository.model.rn;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.repository.model.RnModel;

public class RnVisibleRendition extends RnRendition {

	public RnVisibleRendition() {
		super(RnModel.NAMESPACE.createQName("visibleRendition"));
	}

	protected RnVisibleRendition(QName qName) {
		super(qName);
	}

}
