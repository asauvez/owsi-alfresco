package fr.openwide.alfresco.repo.dictionary.model;

import fr.openwide.alfresco.repo.dictionary.model.owsi.OwsiClassifiable;
import fr.openwide.alfresco.repo.dictionary.model.owsi.OwsiIdentifiable;

public interface OwsiModel {

	String NAMESPACE = "owsi";

	// ---- Aspects

	OwsiClassifiable classifiable = new OwsiClassifiable();
	
	OwsiIdentifiable identifiable = new OwsiIdentifiable();
	
	// ---- Types

}
