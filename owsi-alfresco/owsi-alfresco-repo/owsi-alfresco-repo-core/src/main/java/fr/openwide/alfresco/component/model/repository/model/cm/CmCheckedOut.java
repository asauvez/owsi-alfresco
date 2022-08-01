package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.association.OneToOneAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmCheckedOut extends AspectModel {

	public CmCheckedOut() {
		super(CmModel.NAMESPACE.createQName("checkedOut"));
	}

	protected CmCheckedOut(QName qName) {
		super(qName);
	}

	public final OneToOneAssociationModel workingcopylink = new OneToOneAssociationModel(CmModel.NAMESPACE.createQName("workingcopylink"));
	
	public final CmLockable lockable = addMandatoryAspect(CmModel.lockable);

}
