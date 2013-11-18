package fr.openwide.alfresco.query.core.repository.model.cm;

import fr.openwide.alfresco.query.api.node.model.NameReference;
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
