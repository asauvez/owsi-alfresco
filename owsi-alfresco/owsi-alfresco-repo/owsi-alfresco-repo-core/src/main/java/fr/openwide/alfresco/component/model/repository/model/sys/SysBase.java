package fr.openwide.alfresco.component.model.repository.model.sys;

import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class SysBase extends TypeModel {

	public SysBase() {
		super(NameReference.create(SysModel.NAMESPACE, "base"));
	}

	protected SysBase(NameReference nameReference) {
		super(nameReference);
	}

	public final SysReferenceable referenceable = addMandatoryAspect(SysModel.referenceable);
	
	public final SysLocalized localized = addMandatoryAspect(SysModel.localized);
}
