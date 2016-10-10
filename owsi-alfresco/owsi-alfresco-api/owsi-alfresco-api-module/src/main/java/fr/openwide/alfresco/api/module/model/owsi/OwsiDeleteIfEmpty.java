package fr.openwide.alfresco.api.module.model.owsi;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.module.model.OwsiModel;
import fr.openwide.alfresco.component.model.node.model.AspectModel;

public class OwsiDeleteIfEmpty extends AspectModel {

	public OwsiDeleteIfEmpty() {
		super(NameReference.create(OwsiModel.NAMESPACE, "deleteIfEmpty"));
	}

	protected OwsiDeleteIfEmpty(NameReference nameReference) {
		super(nameReference);
	}

}
