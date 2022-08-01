package fr.openwide.alfresco.component.model.repository.model.emailserver;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.repository.model.EmailServerModel;
import fr.openwide.alfresco.component.model.repository.model.cm.CmEmailed;

public class EmailServerEmailed extends CmEmailed {
	
	public EmailServerEmailed() {
		super(EmailServerModel.NAMESPACE.createQName("emailed"));
	}

	protected EmailServerEmailed(QName qName) {
		super(qName);
	}

}
