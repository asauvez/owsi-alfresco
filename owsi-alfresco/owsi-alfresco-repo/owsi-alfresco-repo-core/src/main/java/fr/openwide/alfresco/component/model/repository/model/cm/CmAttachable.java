package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.association.ManyToManyAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmAttachable extends AspectModel{

	public CmAttachable() {
		super(NameReference.create(CmModel.NAMESPACE, "attachable"));
	}

	protected CmAttachable(NameReference nameReference) {
		super(nameReference);
	}
	
	public final ManyToManyAssociationModel attachments = new ManyToManyAssociationModel(NameReference.create(CmModel.NAMESPACE, "attachments"));
	
}
