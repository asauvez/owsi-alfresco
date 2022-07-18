package fr.openwide.alfresco.component.model.repository.model.sys;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;

public class SysUnmovable extends AspectModel {

	public SysUnmovable() {
		super(NameReference.create(SysModel.NAMESPACE, "unmovable"));
	}

	protected SysUnmovable(NameReference nameReference) {
		super(nameReference);
	}
}
