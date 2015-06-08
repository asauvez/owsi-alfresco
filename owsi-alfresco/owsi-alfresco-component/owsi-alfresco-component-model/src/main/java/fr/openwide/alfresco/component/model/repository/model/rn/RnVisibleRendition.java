package fr.openwide.alfresco.component.model.repository.model.rn;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.repository.model.RnModel;

public class RnVisibleRendition extends RnRendition {

	public RnVisibleRendition() {
		super(NameReference.create(RnModel.NAMESPACE, "visibleRendition"));
	}

	protected RnVisibleRendition(NameReference nameReference) {
		super(nameReference);
	}

}
