package fr.openwide.alfresco.query.core.repository.model.sys;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.core.node.model.TypeModel;
import fr.openwide.alfresco.query.core.repository.model.SysModel;

public class SysBase extends TypeModel {

	public SysBase() {
		super(NameReference.create(SysModel.NAMESPACE, "base"));
	}

	protected SysBase(NameReference nameReference) {
		super(nameReference);
	}

}
