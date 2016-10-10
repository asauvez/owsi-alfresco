package fr.openwide.alfresco.api.module.model;

import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;
import fr.openwide.alfresco.api.module.model.owsi.OwsiClassifiable;
import fr.openwide.alfresco.api.module.model.owsi.OwsiDeleteIfEmpty;
import fr.openwide.alfresco.api.module.model.owsi.OwsiIdentifiable;

public interface OwsiModel {

	NamespaceReference NAMESPACE = NamespaceReference.create("owsi", "http://openwide.fr/model/owsi");
	
	// ---- Aspects

	OwsiClassifiable classifiable = new OwsiClassifiable();
	
	OwsiDeleteIfEmpty deleteIfEmpty = new OwsiDeleteIfEmpty();
	
	OwsiIdentifiable identifiable = new OwsiIdentifiable();
	
	// ---- Types

}
