package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.association.ManyToOneAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class CmCopiedFrom extends AspectModel {

	public CmCopiedFrom() {
		super(NameReference.create(CmModel.NAMESPACE, "copiedfrom"));
	}

	protected CmCopiedFrom(NameReference nameReference) {
		super(nameReference);
	}

	public final ManyToOneAssociationModel original = new ManyToOneAssociationModel(NameReference.create(CmModel.NAMESPACE, "original"));

}
