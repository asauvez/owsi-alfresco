package fr.openwide.alfresco.repo.module.classification.model;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;


public class ClassificationEvent {

	private NodeRef nodeRef;
	private final ClassificationMode mode;
	private ContainerModel model;
	private final Map<QName, Serializable> valuesOverride;
	private final Map<QName, Serializable> before;
	private final Map<QName, Serializable> after;

	public ClassificationEvent(NodeRef nodeRef, ClassificationMode mode) {
		this.nodeRef = nodeRef;
		this.mode = mode;
		this.valuesOverride = null;
		this.before = null;
		this.after = null;
	}
	public ClassificationEvent(NodeRef nodeRef, 
			Map<QName, Serializable> before, Map<QName, Serializable> after) {
		this.nodeRef = nodeRef;
		this.mode = ClassificationMode.UPDATE;
		this.valuesOverride = null;
		this.before = before;
		this.after = after;
	}

	public ClassificationEvent(Map<QName, Serializable> values) {
		this.nodeRef = null;
		this.mode = ClassificationMode.VALUES_OVERRIDE;
		this.model = null;
		this.valuesOverride = values;
		this.before = null;
		this.after = null;
	}

	public NodeRef getNodeRef() {
		if (nodeRef == null) {
			throw new UnsupportedOperationException();
		}
		return nodeRef;
	}
	public void setNodeRef(NodeRef nodeRef) {
		this.nodeRef = nodeRef;
	}
	
	public ClassificationMode getMode() {
		return mode;
	}
	public ContainerModel getModel() {
		return model;
	}
	public void setModel(ContainerModel model) {
		this.model = model;
	}
	public Map<QName, Serializable> getValuesOverride() {
		return valuesOverride;
	}
	
	public Map<QName, Serializable> getBefore() {
		return before;
	}
	public Map<QName, Serializable> getAfter() {
		return after;
	}
}
