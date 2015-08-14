package fr.openwide.alfresco.repo.dictionary.model;

import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;
import fr.openwide.alfresco.repo.dictionary.model.owsi.OwsiClassifiable;
import fr.openwide.alfresco.repo.dictionary.model.owsi.OwsiIdentifiable;

public interface OwsiModel {

	NamespaceReference NAMESPACE = NamespaceReference.create("owsi", "http://openwide.fr/model/owsi");
	
	// ---- Aspects

	OwsiClassifiable classifiable = new OwsiClassifiable();
	
	OwsiIdentifiable identifiable = new OwsiIdentifiable();
	
	// ---- Types

}
