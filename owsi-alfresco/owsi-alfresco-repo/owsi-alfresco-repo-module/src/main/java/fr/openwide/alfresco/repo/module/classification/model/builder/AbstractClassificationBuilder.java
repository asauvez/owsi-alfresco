package fr.openwide.alfresco.repo.module.classification.model.builder;

import java.io.Serializable;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationEvent;
import fr.openwide.alfresco.repo.module.classification.service.impl.ClassificationServiceImpl;

public class AbstractClassificationBuilder<B extends AbstractClassificationBuilder<B>> {

	protected final ClassificationServiceImpl service;
	private final ClassificationEvent event;

	public AbstractClassificationBuilder(ClassificationServiceImpl service, ClassificationEvent event) {
		this.service = service;
		this.event = event;
	}
	
	public NodeModelRepositoryService getNodeModelService() {
		return service.getNodeModelService();
	}
	public <C extends Serializable> C getProperty(SinglePropertyModel<C> property) {
		return getNodeModelService().getProperty(getNodeReference(), property);
	}
	public <E extends Enum<E>> E getProperty(EnumTextPropertyModel<E> property) {
		return getNodeModelService().getProperty(getNodeReference(), property);
	}
	
	@SuppressWarnings("unchecked")
	private B self() {
		return (B) this;
	}
	public ClassificationEvent getEvent() {
		return event;
	}
	public NodeReference getNodeReference() {
		return event.getNodeReference();
	}
	
	public B classificationState(String newState) {
		service.setClassificicationState(getNodeReference(), newState);
		return self();
	}
	public B classificationState(Enum<?> newState) {
		return classificationState(newState.name());
	}
}
