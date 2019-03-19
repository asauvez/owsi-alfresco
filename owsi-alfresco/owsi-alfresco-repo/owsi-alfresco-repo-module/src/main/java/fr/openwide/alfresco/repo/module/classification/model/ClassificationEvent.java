package fr.openwide.alfresco.repo.module.classification.model;

import org.alfresco.service.cmr.repository.NodeRef;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;


public class ClassificationEvent {

	private final NodeRef nodeRef;
	private final ClassificationMode mode;
	private final ContainerModel model;

	public ClassificationEvent(NodeRef nodeRef, ClassificationMode mode, ContainerModel model) {
		this.nodeRef = nodeRef;
		this.mode = mode;
		this.model = model;
	}
	
	public NodeRef getNodeRef() {
		return nodeRef;
	}
	public ClassificationMode getMode() {
		return mode;
	}
	public ContainerModel getModel() {
		return model;
	}
}
