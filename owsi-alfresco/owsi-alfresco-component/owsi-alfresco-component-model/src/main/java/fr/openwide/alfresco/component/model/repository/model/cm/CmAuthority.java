package fr.openwide.alfresco.component.model.repository.model.cm;

import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.sys.SysBase;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class CmAuthority extends SysBase {

	public CmAuthority() {
		super(NameReference.create(CmModel.NAMESPACE, "authority"));
	}

	protected CmAuthority(NameReference nameReference) {
		super(nameReference);
	}

}
