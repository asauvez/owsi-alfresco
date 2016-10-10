package fr.openwide.alfresco.component.model.repository.model;

import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;
import fr.openwide.alfresco.component.model.repository.model.qshare.Shared;

public interface QShareModel {

	// https://svn.alfresco.com/repos/alfresco-open-mirror/alfresco/HEAD/root/projects/repository/config/alfresco/model/quickShareModel.xml
	NamespaceReference NAMESPACE = NamespaceReference.create("qshare", "http://www.alfresco.org/model/qshare/1.0");

	// ---- Aspects

	Shared shared = new Shared();
	
}
