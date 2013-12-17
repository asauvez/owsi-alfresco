package fr.openwide.alfresco.query.core.repository.model.sys;

import fr.openwide.alfresco.query.core.node.model.TypeModel;
import fr.openwide.alfresco.query.core.repository.model.SysModel;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class SysBase extends TypeModel {

	public SysBase() {
		super(NameReference.create(SysModel.NAMESPACE, "base"));
	}

	protected SysBase(NameReference nameReference) {
		super(nameReference);
	}

}
