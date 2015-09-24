package fr.openwide.alfresco.component.model.repository.model.sys;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class SysTemporary extends AspectModel {

	public SysTemporary() {
		super(NameReference.create(SysModel.NAMESPACE, "temporary"));
	}

	protected SysTemporary(NameReference nameReference) {
		super(nameReference);
	}

}
