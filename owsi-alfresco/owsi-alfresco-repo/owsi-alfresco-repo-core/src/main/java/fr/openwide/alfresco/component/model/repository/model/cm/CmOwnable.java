package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmOwnable extends AspectModel {

	public CmOwnable() {
		super(CmModel.NAMESPACE.createQName("ownable"));
	}

	protected CmOwnable(QName qName) {
		super(qName);
	}

	public final TextPropertyModel owner = PropertyModels.newText(this, CmModel.NAMESPACE, "owner");

}
