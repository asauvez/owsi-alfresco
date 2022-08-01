package fr.openwide.alfresco.component.model.repository.model.sys;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;

public class SysHidden extends AspectModel {

	public SysHidden() {
		super(SysModel.NAMESPACE.createQName("hidden"));
	}

	protected SysHidden(QName qName) {
		super(qName);
	}

}
