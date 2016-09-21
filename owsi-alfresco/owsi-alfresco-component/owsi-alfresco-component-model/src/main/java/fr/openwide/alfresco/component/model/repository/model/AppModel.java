package fr.openwide.alfresco.component.model.repository.model;

import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;
import fr.openwide.alfresco.component.model.repository.model.app.AppInlineEditable;

public interface AppModel {

	// https://svn.alfresco.com/repos/alfresco-open-mirror/alfresco/HEAD/root/projects/repository/config/alfresco/model/applicationModel.xml
	NamespaceReference NAMESPACE = NamespaceReference.create("app", "http://www.alfresco.org/model/application/1.0");

	// ---- Aspects

	AppInlineEditable inlineeditable = new AppInlineEditable();
	
	// ---- Types

	}
