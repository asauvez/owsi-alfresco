package fr.openwide.alfresco.component.model.repository.model.usr;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.repository.model.UsrModel;
import fr.openwide.alfresco.component.model.repository.model.sys.SysBase;

public class UsrAuthority extends SysBase {

	public UsrAuthority() {
		super(NameReference.create(UsrModel.NAMESPACE, "authority"));
	}

	protected UsrAuthority(NameReference nameReference) {
		super(nameReference);
	}
}
