package fr.openwide.alfresco.component.model.repository.model.sys;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;

public class SysUndeletable extends AspectModel {

	public SysUndeletable() {
		super(NameReference.create(SysModel.NAMESPACE, "undeletable"));
	}

	protected SysUndeletable(NameReference nameReference) {
		super(nameReference);
	}
}
