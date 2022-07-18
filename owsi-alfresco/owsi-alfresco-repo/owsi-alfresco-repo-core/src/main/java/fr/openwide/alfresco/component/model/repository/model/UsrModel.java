package fr.openwide.alfresco.component.model.repository.model;

import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;
import fr.openwide.alfresco.component.model.repository.model.usr.UsrAuthority;
import fr.openwide.alfresco.component.model.repository.model.usr.UsrAuthorityContainer;
import fr.openwide.alfresco.component.model.repository.model.usr.UsrUser;

// https://github.com/Alfresco/alfresco-repository/blob/develop/src/main/resources/org/alfresco/repo/security/authentication/userModel.xml
public interface UsrModel {

	NamespaceReference NAMESPACE = NamespaceReference.create("usr", "http://www.alfresco.org/model/user/1.0");

	// ---- Types

	UsrAuthority authority = new UsrAuthority();
	UsrUser user = new UsrUser();
	UsrAuthorityContainer authorityContainer = new UsrAuthorityContainer();
}
