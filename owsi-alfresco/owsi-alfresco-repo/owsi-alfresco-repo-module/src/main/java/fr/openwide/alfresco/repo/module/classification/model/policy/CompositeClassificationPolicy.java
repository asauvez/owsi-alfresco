package fr.openwide.alfresco.repo.module.classification.model.policy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationEvent;
import fr.openwide.alfresco.repo.module.classification.model.builder.ClassificationBuilder;

public class CompositeClassificationPolicy<T extends ContainerModel> implements ClassificationPolicy<T> {

	private List<ConditionalClassificationPolicy<? super T>> policies = new ArrayList<>();
	
	public CompositeClassificationPolicy<T> add(Predicate<? super BusinessNode> predicate, ClassificationPolicy<? super T> policy) {
		policies.add(new ConditionalClassificationPolicy<>(predicate, policy));
		return this;
	}
	public CompositeClassificationPolicy<T> add(Predicate<? super BusinessNode> predicate, Consumer<ClassificationBuilder> consumer) {
		return add(predicate, new ConsumerClassificationPolicy<T>(consumer));
	}
	public void add(ClassificationPolicy<? super T> policy) {
		add(node -> true, policy);
	}
	public void add(Consumer<ClassificationBuilder> consumer) {
		add(new ConsumerClassificationPolicy<T>(consumer));
	}
	
	@Override
	public void initNodeScopeBuilder(NodeScopeBuilder nodeScopeBuilder) {
		for (ConditionalClassificationPolicy<? super T> policy : policies) {
			policy.initNodeScopeBuilder(nodeScopeBuilder);
		}
	}

	@Override
	public void classify(ClassificationBuilder builder, T model, ClassificationEvent event) {
		for (ConditionalClassificationPolicy<? super T> policy : policies) {
			if (policy.tryToClassify(builder, model, event)) {
				break;
			}
		}
	}
}
