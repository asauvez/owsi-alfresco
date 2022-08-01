package fr.openwide.alfresco.component.model.repository.model.sys;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;

public class SysContainer extends SysBase {

	public SysContainer() {
		super(SysModel.NAMESPACE.createQName("container"));
	}

	protected SysContainer(QName qName) {
		super(qName);
	}

	public final ChildAssociationModel children = new ChildAssociationModel(SysModel.NAMESPACE.createQName("children"));
}
