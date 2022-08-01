package fr.openwide.alfresco.component.model.repository.model.sys;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.LongPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;

public class SysCascadeUpdate extends AspectModel {

	public SysCascadeUpdate() {
		super(SysModel.NAMESPACE.createQName("cascadeUpdate"));
	}

	protected SysCascadeUpdate(QName qName) {
		super(qName);
	}

	public final LongPropertyModel cascadeCRC = PropertyModels.newLong(this, SysModel.NAMESPACE, "cascadeCRC");

	public final LongPropertyModel cascadeTx = PropertyModels.newLong(this, SysModel.NAMESPACE, "cascadeTx");
}
