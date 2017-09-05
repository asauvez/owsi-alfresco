package fr.openwide.alfresco.component.model.repository.model;

import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;
import fr.openwide.alfresco.component.model.repository.model.rn.CmThumbnail;
import fr.openwide.alfresco.component.model.repository.model.rn.RnHiddenRendition;
import fr.openwide.alfresco.component.model.repository.model.rn.RnPreventRenditions;
import fr.openwide.alfresco.component.model.repository.model.rn.RnRendition;
import fr.openwide.alfresco.component.model.repository.model.rn.RnRenditioned;
import fr.openwide.alfresco.component.model.repository.model.rn.RnVisibleRendition;

// https://github.com/Alfresco/alfresco-repository/blob/develop/src/main/resources/alfresco/model/contentModel.xml
public interface RnModel {

	NamespaceReference NAMESPACE = NamespaceReference.create("rn", "http://www.alfresco.org/model/rendition/1.0");

	// ---- Aspects

	RnRendition rendition = new RnRendition();
	RnHiddenRendition hiddenRendition = new RnHiddenRendition();
	RnVisibleRendition visibleRendition = new RnVisibleRendition();
	RnPreventRenditions preventRenditions = new RnPreventRenditions();

	RnRenditioned renditioned = new RnRenditioned();
	
	// ---- Types

	CmThumbnail thumbnail = new CmThumbnail();

}
