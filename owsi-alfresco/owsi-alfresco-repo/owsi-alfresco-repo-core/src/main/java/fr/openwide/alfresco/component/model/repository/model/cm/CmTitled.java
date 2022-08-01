package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmTitled extends AspectModel {

	public CmTitled() {
		super(CmModel.NAMESPACE.createQName("titled"));
	}

	protected CmTitled(QName qName) {
		super(qName);
	}

	public final TextPropertyModel title = PropertyModels.newText(this, CmModel.NAMESPACE, "title");
	
	public final TextPropertyModel description = PropertyModels.newText(this, CmModel.NAMESPACE, "description");

}
