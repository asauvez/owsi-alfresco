package fr.openwide.alfresco.repo.module.classification.model;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;


public class ClassificationEvent {

	private final NodeReference nodeReference;
	private final ClassificationMode mode;
	private final ContainerModel model;

	public ClassificationEvent(NodeReference nodeReference, ClassificationMode mode, ContainerModel model) {
		this.nodeReference = nodeReference;
		this.mode = mode;
		this.model = model;
	}
	
	public NodeReference getNodeReference() {
		return nodeReference;
	}
	public ClassificationMode getMode() {
		return mode;
	}
	public ContainerModel getModel() {
		return model;
	}
}
