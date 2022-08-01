package fr.openwide.alfresco.repo.module.classification.model.builder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
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
	@SuppressWarnings("unchecked")
	public <C extends Serializable> C getProperty(QName property) {
		if (event.getValuesOverride() != null) {
			return (C) event.getValuesOverride().get(property);
		} else {
			return getNodeModelService().getProperty(getNodeRef(), property);
		}
	}
	public <C extends Serializable> C getProperty(SinglePropertyModel<C> property) {
		if (event.getValuesOverride() != null) {
			return getNodeModelService().getProperty(event.getValuesOverride(), property);
		} else {
			return getNodeModelService().getProperty(getNodeRef(), property);
		}
	}
	public <C extends Serializable> List<C> getProperty(MultiPropertyModel<C> property) {
		if (event.getValuesOverride() != null) {
			return getNodeModelService().getProperty(event.getValuesOverride(), property);
		} else {
			return getNodeModelService().getProperty(getNodeRef(), property);
		}
	}
	public <E extends Enum<E>> E getProperty(EnumTextPropertyModel<E> property) {
		if (event.getValuesOverride() != null) {
			return getNodeModelService().getProperty(event.getValuesOverride(), property);
		} else {
			return getNodeModelService().getProperty(getNodeRef(), property);
		}
	}
	
	public boolean hasPropertiesChanged(PropertyModel<?> ... properties) {
		Map<QName, Serializable> before = event.getBefore();
		Map<QName, Serializable> after = event.getAfter();
		if (before == null || after == null) return true;
		
		for (PropertyModel<?> property : properties) {
			Serializable beforeValue = before.get(property.getQName());
			Serializable  afterValue =  after.get(property.getQName());
			if (! Objects.equals(beforeValue, afterValue)) {
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	private B self() {
		return (B) this;
	}
	public ClassificationEvent getEvent() {
		return event;
	}
	public NodeRef getNodeRef() {
		return event.getNodeRef();
	}
	
	public B classificationState(String newState) {
		service.setClassificicationState(getNodeRef(), newState);
		return self();
	}
	public B classificationState(Enum<?> newState) {
		return classificationState(newState.name());
	}
	
	public PreviousVersionBuilder<B> getPreviousNodeWith(SinglePropertyModel<?> ... properties) {
		return new PreviousVersionBuilder<B>(self(), properties);
	}
}
