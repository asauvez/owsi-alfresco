package fr.openwide.alfresco.repo.module.classification.model.policy;

import java.util.function.Consumer;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationEvent;
import fr.openwide.alfresco.repo.module.classification.model.builder.ClassificationBuilder;

public class ConsumerClassificationPolicy<T extends ContainerModel> implements ClassificationPolicy<T> {

	private Consumer<ClassificationBuilder> consumer;

	public ConsumerClassificationPolicy(Consumer<ClassificationBuilder> consumer) {
		this.consumer = consumer;
	}
	
	@Override
	public void classify(ClassificationBuilder builder, T model, ClassificationEvent event) {
		consumer.accept(builder);
	}
}
