package fr.openwide.alfresco.component.model.repository.model;

import fr.openwide.alfresco.component.model.repository.model.rn.CmThumbnail;
import fr.openwide.alfresco.component.model.repository.model.rn.RnHiddenRendition;
import fr.openwide.alfresco.component.model.repository.model.rn.RnRendition;
import fr.openwide.alfresco.component.model.repository.model.rn.RnRenditioned;
import fr.openwide.alfresco.component.model.repository.model.rn.RnVisibleRendition;

public interface RnModel {

	String NAMESPACE = "rn";

	// ---- Aspects

	RnRendition rendition = new RnRendition();
	RnHiddenRendition hiddenRendition = new RnHiddenRendition();
	RnVisibleRendition visibleRendition = new RnVisibleRendition();

	RnRenditioned renditioned = new RnRenditioned();
	
	// ---- Types

	CmThumbnail thumbnail = new CmThumbnail();

}
