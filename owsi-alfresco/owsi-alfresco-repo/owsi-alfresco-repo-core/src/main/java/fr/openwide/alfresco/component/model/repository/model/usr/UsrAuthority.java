package fr.openwide.alfresco.component.model.repository.model.usr;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.repository.model.UsrModel;
import fr.openwide.alfresco.component.model.repository.model.sys.SysBase;

public class UsrAuthority extends SysBase {

	public UsrAuthority() {
		super(UsrModel.NAMESPACE.createQName("authority"));
	}

	protected UsrAuthority(QName qName) {
		super(qName);
	}
}
