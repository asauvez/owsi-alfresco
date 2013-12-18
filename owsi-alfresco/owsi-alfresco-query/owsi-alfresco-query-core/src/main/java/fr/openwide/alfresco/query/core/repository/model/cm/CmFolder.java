package fr.openwide.alfresco.query.core.repository.model.cm;

import fr.openwide.alfresco.query.core.node.model.ChildAssociationModel;
import fr.openwide.alfresco.query.core.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class CmFolder extends CmObject {

	public CmFolder() {
		super(NameReference.create(CmModel.NAMESPACE, "folder"));
	}

	protected CmFolder(NameReference nameReference) {
		super(nameReference);
	}

	public ChildAssociationModel contains = new ChildAssociationModel(NameReference.create(CmModel.NAMESPACE, "contains"));
}
