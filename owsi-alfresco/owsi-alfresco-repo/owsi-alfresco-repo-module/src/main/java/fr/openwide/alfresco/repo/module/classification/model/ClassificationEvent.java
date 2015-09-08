package fr.openwide.alfresco.repo.module.classification.model;

import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;


public class ClassificationEvent {

	private final BusinessNode node;
	private final ClassificationMode mode;
	private final ContainerModel model;

	public ClassificationEvent(BusinessNode node, ClassificationMode mode, ContainerModel model) {
		this.node = node;
		this.mode = mode;
		this.model = model;
	}
	
	public BusinessNode getNode() {
		return node;
	}
	public ClassificationMode getMode() {
		return mode;
	}
	public ContainerModel getModel() {
		return model;
	}
}
