package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmCategoryRoot extends CmObject {

	public CmCategoryRoot() {
		super(CmModel.NAMESPACE.createQName("category_root"));
	}

	protected CmCategoryRoot(QName qName) {
		super(qName);
	}

	public final ChildAssociationModel categories = new ChildAssociationModel(CmModel.NAMESPACE.createQName("categories"));
}
