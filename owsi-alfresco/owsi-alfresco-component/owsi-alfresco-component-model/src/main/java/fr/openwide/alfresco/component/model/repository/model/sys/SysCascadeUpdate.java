package fr.openwide.alfresco.component.model.repository.model.sys;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.LongPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;

public class SysCascadeUpdate extends AspectModel {

	public SysCascadeUpdate() {
		super(NameReference.create(SysModel.NAMESPACE, "cascadeUpdate"));
	}

	protected SysCascadeUpdate(NameReference nameReference) {
		super(nameReference);
	}

	public final LongPropertyModel cascadeCRC = PropertyModels.newLong(this, SysModel.NAMESPACE, "cascadeCRC");

	public final LongPropertyModel cascadeTx = PropertyModels.newLong(this, SysModel.NAMESPACE, "cascadeTx");
}
