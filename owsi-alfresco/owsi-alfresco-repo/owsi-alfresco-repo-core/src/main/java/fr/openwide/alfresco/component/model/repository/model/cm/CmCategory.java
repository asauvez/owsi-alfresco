package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmCategory extends CmObject {

	public CmCategory() {
		super(CmModel.NAMESPACE.createQName("category"));
	}

	protected CmCategory(QName qName) {
		super(qName);
	}

	public final ChildAssociationModel subcategories = new ChildAssociationModel(CmModel.NAMESPACE.createQName("subcategories"));
}
