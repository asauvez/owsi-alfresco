package fr.openwide.alfresco.repo.module.classification.model;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;


public class ClassificationEvent {

	private final NodeRef nodeRef;
	private final ClassificationMode mode;
	private final ContainerModel model;
	private final Map<QName, Serializable> valuesOverride;

	public ClassificationEvent(NodeRef nodeRef, ClassificationMode mode, ContainerModel model) {
		this.nodeRef = nodeRef;
		this.mode = mode;
		this.model = model;
		this.valuesOverride = null;
	}

	public ClassificationEvent(Map<QName, Serializable> values) {
		this.nodeRef = null;
		this.mode = null;
		this.model = null;
		this.valuesOverride = values;
	}

	public NodeRef getNodeRef() {
		if (nodeRef == null) {
			throw new UnsupportedOperationException();
		}
		return nodeRef;
	}
	public ClassificationMode getMode() {
		return mode;
	}
	public ContainerModel getModel() {
		return model;
	}
	public Map<QName, Serializable> getValuesOverride() {
		return valuesOverride;
	}
}
