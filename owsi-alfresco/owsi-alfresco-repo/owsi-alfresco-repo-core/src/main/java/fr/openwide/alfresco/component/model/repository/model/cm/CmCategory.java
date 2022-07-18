package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class CmCategory extends CmObject {

	public CmCategory() {
		super(NameReference.create(CmModel.NAMESPACE, "category"));
	}

	protected CmCategory(NameReference nameReference) {
		super(nameReference);
	}

	public final ChildAssociationModel subcategories = new ChildAssociationModel(NameReference.create(CmModel.NAMESPACE, "subcategories"));
}
