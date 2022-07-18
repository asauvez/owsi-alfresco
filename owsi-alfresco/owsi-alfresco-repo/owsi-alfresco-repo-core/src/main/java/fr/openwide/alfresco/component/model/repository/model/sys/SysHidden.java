package fr.openwide.alfresco.component.model.repository.model.sys;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;

public class SysHidden extends AspectModel {

	public SysHidden() {
		super(NameReference.create(SysModel.NAMESPACE, "hidden"));
	}

	protected SysHidden(NameReference nameReference) {
		super(nameReference);
	}

}
