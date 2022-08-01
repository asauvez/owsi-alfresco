package fr.openwide.alfresco.component.model.repository.model.sys;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;

public class SysBase extends TypeModel {

	public SysBase() {
		super(SysModel.NAMESPACE.createQName("base"));
	}

	protected SysBase(QName qName) {
		super(qName);
	}

	public final SysReferenceable referenceable = addMandatoryAspect(SysModel.referenceable);
	
	public final SysLocalized localized = addMandatoryAspect(SysModel.localized);
}
