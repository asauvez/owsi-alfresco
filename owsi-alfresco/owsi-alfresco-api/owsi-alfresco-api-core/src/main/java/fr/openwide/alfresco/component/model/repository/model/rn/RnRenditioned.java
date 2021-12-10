package fr.openwide.alfresco.component.model.repository.model.rn;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.RnModel;

public class RnRenditioned extends AspectModel {

	public RnRenditioned() {
		super(NameReference.create(RnModel.NAMESPACE, "renditioned"));
	}

	protected RnRenditioned(NameReference nameReference) {
		super(nameReference);
	}

	public final ChildAssociationModel rendition = new ChildAssociationModel(NameReference.create(RnModel.NAMESPACE, "rendition"));
}
