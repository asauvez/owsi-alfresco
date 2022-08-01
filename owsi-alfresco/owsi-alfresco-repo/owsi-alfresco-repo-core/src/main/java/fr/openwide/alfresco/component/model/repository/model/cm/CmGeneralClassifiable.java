package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiNodeRefPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmGeneralClassifiable extends AspectModel {

	public CmGeneralClassifiable() {
		super(CmModel.NAMESPACE.createQName("generalclassifiable"));
	}

	protected CmGeneralClassifiable(QName qName) {
		super(qName);
	}

	// vrai type = d:category
	public final MultiNodeRefPropertyModel categories = PropertyModels.newMultiNodeRef(this, CmModel.NAMESPACE, "categories");
}
