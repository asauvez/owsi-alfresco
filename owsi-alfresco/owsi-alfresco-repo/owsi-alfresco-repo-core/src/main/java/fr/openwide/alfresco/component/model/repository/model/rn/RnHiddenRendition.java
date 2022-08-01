package fr.openwide.alfresco.component.model.repository.model.rn;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.repository.model.RnModel;

public class RnHiddenRendition extends RnRendition {

	public RnHiddenRendition() {
		super(RnModel.NAMESPACE.createQName("hiddenRendition"));
	}

	protected RnHiddenRendition(QName qName) {
		super(qName);
	}

}
