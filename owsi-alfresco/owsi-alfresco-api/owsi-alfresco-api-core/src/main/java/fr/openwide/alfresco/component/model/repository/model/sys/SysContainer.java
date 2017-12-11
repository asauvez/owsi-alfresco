package fr.openwide.alfresco.component.model.repository.model.sys;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;

public class SysContainer extends SysBase {

	public SysContainer() {
		super(NameReference.create(SysModel.NAMESPACE, "container"));
	}

	protected SysContainer(NameReference nameReference) {
		super(nameReference);
	}

	public final ChildAssociationModel children = new ChildAssociationModel(NameReference.create(SysModel.NAMESPACE, "children"));
}
