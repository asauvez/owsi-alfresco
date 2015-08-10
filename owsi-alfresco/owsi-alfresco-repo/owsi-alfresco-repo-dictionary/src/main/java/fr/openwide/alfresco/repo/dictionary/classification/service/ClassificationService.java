package fr.openwide.alfresco.repo.dictionary.classification.service;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.repo.dictionary.classification.model.ClassificationPolicy;

public interface ClassificationService {

	<T extends ContainerModel> void addClassification(T model, ClassificationPolicy<T> policy);

}
