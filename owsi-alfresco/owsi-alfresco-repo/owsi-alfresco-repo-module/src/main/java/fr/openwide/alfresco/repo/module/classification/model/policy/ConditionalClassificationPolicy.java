package fr.openwide.alfresco.repo.module.classification.model.policy;

import java.util.function.Predicate;

import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationEvent;
import fr.openwide.alfresco.repo.module.classification.model.builder.ClassificationBuilder;

public class ConditionalClassificationPolicy<T extends ContainerModel> implements ClassificationPolicy<T> {

	private Predicate<? super BusinessNode> predicate;
	private ClassificationPolicy<? super T> policy;

	public ConditionalClassificationPolicy(Predicate<? super BusinessNode> predicate, ClassificationPolicy<? super T> policy) {
		this.predicate = predicate;
		this.policy = policy;
	}
	
	@Override
	public void initNodeScopeBuilder(NodeScopeBuilder nodeScopeBuilder) {
		policy.initNodeScopeBuilder(nodeScopeBuilder);
	}

	@Override
	public void classify(ClassificationBuilder builder, T model, ClassificationEvent event) {
	}
	public boolean tryToClassify(ClassificationBuilder builder, T model, ClassificationEvent event) {
		if (predicate.test(event.getNode())) {
			policy.classify(builder, model, event);
			return true;
		}
		return false;
	}
}
