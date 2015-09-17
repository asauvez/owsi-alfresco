package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.AssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmCheckedOut extends AspectModel {

	public CmCheckedOut() {
		super(NameReference.create(CmModel.NAMESPACE, "checkedOut"));
	}

	protected CmCheckedOut(NameReference nameReference) {
		super(nameReference);
	}

	public final AssociationModel workingcopylink = new AssociationModel(NameReference.create(CmModel.NAMESPACE, "workingcopylink"));
	
	public final CmLockable lockable = addMandatoryAspect(CmModel.lockable);

}
