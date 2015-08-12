package fr.openwide.alfresco.repo.dictionary.classification.model;

import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;

public interface ClassificationPolicy<T extends ContainerModel> {

	void initNodeScopeBuilder(NodeScopeBuilder nodeScopeBuilder);

	void classify(ClassificationBuilder builder, T model, BusinessNode node, boolean update);

}
