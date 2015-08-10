package fr.openwide.alfresco.repo.dictionary.classification.model;

import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public interface ClassificationPolicy<T extends ContainerModel> {

	void classify(ClassificationBuilder builder, T model, BusinessNode node, boolean update); 
}
