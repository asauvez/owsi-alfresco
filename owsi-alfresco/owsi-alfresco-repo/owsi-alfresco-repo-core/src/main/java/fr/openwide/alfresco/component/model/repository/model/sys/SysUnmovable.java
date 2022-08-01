package fr.openwide.alfresco.component.model.repository.model.sys;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;

public class SysUnmovable extends AspectModel {

	public SysUnmovable() {
		super(SysModel.NAMESPACE.createQName("unmovable"));
	}

	protected SysUnmovable(QName qName) {
		super(qName);
	}
}
