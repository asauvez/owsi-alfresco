package fr.openwide.alfresco.component.model.repository.model.cm;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.sys.SysBase;

public class CmAuthority extends SysBase {

	public CmAuthority() {
		super(CmModel.NAMESPACE.createQName("authority"));
	}

	protected CmAuthority(QName qName) {
		super(qName);
	}

}
