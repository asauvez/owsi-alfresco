package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class CmCategoryRoot extends CmObject {

	public CmCategoryRoot() {
		super(NameReference.create(CmModel.NAMESPACE, "category_root"));
	}

	protected CmCategoryRoot(NameReference nameReference) {
		super(nameReference);
	}

	public final ChildAssociationModel categories = new ChildAssociationModel(NameReference.create(CmModel.NAMESPACE, "categories"));
}
