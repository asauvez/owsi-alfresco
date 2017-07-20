package fr.openwide.alfresco.component.model.repository.model.sys;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.LongPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class SysReferenceable extends AspectModel {

	public SysReferenceable() {
		super(NameReference.create(SysModel.NAMESPACE, "referenceable"));
	}

	protected SysReferenceable(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel storeProtocol = PropertyModels.newText(this, SysModel.NAMESPACE, "store-protocol");
	
	public final TextPropertyModel storeIdentifier = PropertyModels.newText(this, SysModel.NAMESPACE, "store-identifier");

	public final TextPropertyModel nodeUuid = PropertyModels.newText(this, SysModel.NAMESPACE, "node-uuid");

	public final LongPropertyModel nodeDbid = PropertyModels.newLong(this, SysModel.NAMESPACE, "node-dbid");

}
