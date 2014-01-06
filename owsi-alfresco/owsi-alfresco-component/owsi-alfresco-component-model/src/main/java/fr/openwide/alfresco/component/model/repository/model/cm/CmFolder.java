package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class CmFolder extends CmObject {

	public CmFolder() {
		super(NameReference.create(CmModel.NAMESPACE, "folder"));
	}

	protected CmFolder(NameReference nameReference) {
		super(nameReference);
	}

	public final ChildAssociationModel contains = new ChildAssociationModel(NameReference.create(CmModel.NAMESPACE, "contains"));
}
