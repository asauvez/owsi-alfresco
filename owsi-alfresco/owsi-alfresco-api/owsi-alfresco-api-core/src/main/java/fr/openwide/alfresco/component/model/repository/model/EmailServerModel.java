package fr.openwide.alfresco.component.model.repository.model;

import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;
import fr.openwide.alfresco.component.model.repository.model.emailserver.EmailServerAliasable;
import fr.openwide.alfresco.component.model.repository.model.emailserver.EmailServerEmailed;

public interface EmailServerModel {

	// https://svn.alfresco.com/repos/alfresco-open-mirror/alfresco/HEAD/root/projects/repository/config/alfresco/model/emailServerModel.xml
	NamespaceReference NAMESPACE = NamespaceReference.create("emailserver", "http://www.alfresco.org/model/emailserver/1.0");

	// ---- Aspects

	EmailServerEmailed emailed = new EmailServerEmailed();
	EmailServerAliasable aliasable = new EmailServerAliasable();

	// ---- Types

}
