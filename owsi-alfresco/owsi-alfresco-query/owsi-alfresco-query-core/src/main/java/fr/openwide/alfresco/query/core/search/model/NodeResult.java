package fr.openwide.alfresco.query.core.search.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import fr.openwide.alfresco.query.core.node.model.TypeModel;
import fr.openwide.alfresco.query.core.node.model.property.PropertyModel;
import fr.openwide.alfresco.query.core.node.model.value.NameReference;
import fr.openwide.alfresco.query.core.node.model.value.NodeReference;

public class NodeResult implements Serializable {

	private static final long serialVersionUID = 6930653481257487738L;

	private final NodeReference reference;
	private final TypeModel type;
	private final Map<NameReference, Object> properties = new LinkedHashMap<NameReference, Object>();
	private final Set<NameReference> aspects = new LinkedHashSet<NameReference>();

	public NodeResult(NodeReference nodeReference, TypeModel type) {
		this.reference = nodeReference;
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	public <C> C get(PropertyModel<C> property) {
		return (C) properties.get(property.getNameReference());
	}
	public <C> NodeResult put(PropertyModel<C> property, C value) {
		properties.put(property.getNameReference(), value);
		return this;
	}

	public NodeReference getReference() {
		return reference;
	}
	public TypeModel getType() {
		return type;
	}
	public Map<NameReference, Object> getProperties() {
		return properties;
	}
	public Set<NameReference> getAspects() {
		return aspects;
	}

}
