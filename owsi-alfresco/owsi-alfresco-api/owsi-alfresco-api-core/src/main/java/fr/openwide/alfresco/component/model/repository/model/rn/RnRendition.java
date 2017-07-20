package fr.openwide.alfresco.component.model.repository.model.rn;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.repository.model.RnModel;

public class RnRendition extends AspectModel {

	public RnRendition() {
		super(NameReference.create(RnModel.NAMESPACE, "rendition"));
	}

	protected RnRendition(NameReference nameReference) {
		super(nameReference);
	}

}
