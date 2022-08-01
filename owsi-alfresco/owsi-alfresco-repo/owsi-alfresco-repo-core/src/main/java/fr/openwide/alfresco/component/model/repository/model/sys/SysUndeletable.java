package fr.openwide.alfresco.component.model.repository.model.sys;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;

public class SysUndeletable extends AspectModel {

	public SysUndeletable() {
		super(SysModel.NAMESPACE.createQName("undeletable"));
	}

	protected SysUndeletable(QName qName) {
		super(qName);
	}
}
