package fr.openwide.alfresco.component.model.node.model.bean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.embed.PropertiesNode;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.NodeReferencePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;

public abstract class NodeBean {
	
	private Map<NameReference, Serializable> properties;
	
	public Map<NameReference, Serializable> getProperties() {
		return properties;
	}
	public NodeBean merge(NodeBean otherBean) {
		this.properties.putAll(otherBean.getProperties());
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public <C extends Serializable> C getProperty(SinglePropertyModel<C> property) {
		return (C) properties.get(property.getNameReference());
	}
	@SuppressWarnings("unchecked")
	public <C extends Serializable> List<C> getProperty(MultiPropertyModel<C> property) {
		return (List<C>) properties.get(property.getNameReference());
	}
	public <E extends Enum<E>> E getProperty(EnumTextPropertyModel<E> property) {
		return PropertiesNode.textToEnum(property, (String) properties.get(property.getNameReference()));
	}
	
	public <C extends Serializable> void setProperty(SinglePropertyModel<C> property, C value) {
		properties.put(property.getNameReference(), value);
	}
	public <C extends Serializable> void setProperty(MultiPropertyModel<C> property, List<C> value) {
		properties.put(property.getNameReference(), (Serializable) value);
	}
	public <E extends Enum<E>> void setProperty(EnumTextPropertyModel<E> property, E value) {
		properties.put(property.getNameReference(), PropertiesNode.enumToText(value));
	}
	public void setProperty(NodeReferencePropertyModel property, NodeReference value) {
		properties.put(property.getNameReference(), value);
	}
	
	public void unsetProperty(PropertyModel<?> property) {
		properties.remove(property.getNameReference());
	}
}
