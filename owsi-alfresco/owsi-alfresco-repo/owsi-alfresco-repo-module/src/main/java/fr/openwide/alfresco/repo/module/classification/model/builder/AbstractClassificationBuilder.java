package fr.openwide.alfresco.repo.module.classification.model.builder;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationEvent;
import fr.openwide.alfresco.repo.module.classification.service.impl.ClassificationServiceImpl;

public class AbstractClassificationBuilder<B extends AbstractClassificationBuilder<B>> {

	protected final ClassificationServiceImpl service;
	private final ClassificationEvent event;

	public AbstractClassificationBuilder(ClassificationServiceImpl service, ClassificationEvent event) {
		this.service = service;
		this.event = event;
	}
	
	@SuppressWarnings("unchecked")
	private B self() {
		return (B) this;
	}
	public ClassificationEvent getEvent() {
		return event;
	}
	public BusinessNode getNode() {
		return event.getNode();
	}
	public NodeReference getNodeReference() {
		return getNode().getNodeReference();
	}
	
	public B classificationState(String newState) {
		service.setClassificicationState(getNode().getNodeReference(), newState);
		return self();
	}
	public B classificationState(Enum<?> newState) {
		return classificationState(newState.name());
	}
}
