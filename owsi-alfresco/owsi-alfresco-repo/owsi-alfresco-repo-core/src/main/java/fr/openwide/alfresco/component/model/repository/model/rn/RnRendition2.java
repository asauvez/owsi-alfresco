package fr.openwide.alfresco.component.model.repository.model.rn;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.IntegerPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.RnModel;

public class RnRendition2 extends AspectModel {

	public RnRendition2() {
		super(RnModel.NAMESPACE.createQName("rendition2"));
	}

	protected RnRendition2(QName qName) {
		super(qName);
	}

	public final IntegerPropertyModel contentUrlHashCode = PropertyModels.newInteger(this, RnModel.NAMESPACE, "contentUrlHashCode");
}
