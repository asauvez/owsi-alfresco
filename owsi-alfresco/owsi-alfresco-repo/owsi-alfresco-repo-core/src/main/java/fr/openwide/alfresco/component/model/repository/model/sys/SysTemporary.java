package fr.openwide.alfresco.component.model.repository.model.sys;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;

public class SysTemporary extends AspectModel {

	public SysTemporary() {
		super(SysModel.NAMESPACE.createQName("temporary"));
	}

	protected SysTemporary(QName qName) {
		super(qName);
	}

}
