package fr.openwide.alfresco.component.model.repository.model.sys;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.LongPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;

public class SysReferenceable extends AspectModel {

	public SysReferenceable() {
		super(SysModel.NAMESPACE.createQName("referenceable"));
	}

	protected SysReferenceable(QName qName) {
		super(qName);
	}

	public final TextPropertyModel storeProtocol = PropertyModels.newText(this, SysModel.NAMESPACE, "store-protocol");
	
	public final TextPropertyModel storeIdentifier = PropertyModels.newText(this, SysModel.NAMESPACE, "store-identifier");

	public final TextPropertyModel nodeUuid = PropertyModels.newText(this, SysModel.NAMESPACE, "node-uuid");

	public final LongPropertyModel nodeDbid = PropertyModels.newLong(this, SysModel.NAMESPACE, "node-dbid");

}
