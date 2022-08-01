package fr.openwide.alfresco.component.model.repository.model.rn;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.repository.model.RnModel;

public class RnPreventRenditions extends AspectModel {

	public RnPreventRenditions() {
		super(RnModel.NAMESPACE.createQName("preventRenditions"));
	}

	protected RnPreventRenditions(QName qName) {
		super(qName);
	}
}
