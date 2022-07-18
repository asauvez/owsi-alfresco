package fr.openwide.alfresco.component.model.repository.model.emailserver;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.repository.model.EmailServerModel;
import fr.openwide.alfresco.component.model.repository.model.cm.CmEmailed;

public class EmailServerEmailed extends CmEmailed {
	
	public EmailServerEmailed() {
		super(NameReference.create(EmailServerModel.NAMESPACE, "emailed"));
	}

	protected EmailServerEmailed(NameReference nameReference) {
		super(nameReference);
	}

}
