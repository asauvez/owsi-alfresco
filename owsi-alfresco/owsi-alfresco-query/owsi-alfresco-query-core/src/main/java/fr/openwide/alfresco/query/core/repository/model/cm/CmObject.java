package fr.openwide.alfresco.query.core.repository.model.cm;

import fr.openwide.alfresco.query.core.node.model.property.PropertyModels;
import fr.openwide.alfresco.query.core.node.model.property.TextPropertyModel;
import fr.openwide.alfresco.query.core.repository.model.CmModel;
import fr.openwide.alfresco.query.core.repository.model.sys.SysBase;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class CmObject extends SysBase {

	public CmObject() {
		super(NameReference.create(CmModel.NAMESPACE, "cmobject"));
	}

	protected CmObject(NameReference nameReference) {
		super(nameReference);
	}

	public final TextPropertyModel name = PropertyModels.newText(this, CmModel.NAMESPACE, "name");

}
