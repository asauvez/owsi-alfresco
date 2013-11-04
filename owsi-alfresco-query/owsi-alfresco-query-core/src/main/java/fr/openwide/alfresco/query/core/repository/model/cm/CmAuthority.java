package fr.openwide.alfresco.query.core.repository.model.cm;

import fr.openwide.alfresco.query.core.node.model.value.NameReference;
import fr.openwide.alfresco.query.core.repository.model.CmModel;
import fr.openwide.alfresco.query.core.repository.model.sys.SysBase;

public class CmAuthority extends SysBase {

	public CmAuthority() {
		super(NameReference.create(CmModel.NAMESPACE, "authority"));
	}

	protected CmAuthority(NameReference nameReference) {
		super(nameReference);
	}

}
