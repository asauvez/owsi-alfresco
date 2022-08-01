package fr.openwide.alfresco.component.model.node.model.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.property.PropertyEnumeration;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;

public abstract class NodeBean {
	
	private Map<QName, Serializable> properties = new HashMap<>();
	
	public Map<QName, Serializable> getProperties() {
		return properties;
	}
	public void setProperties(Map<QName, Serializable> properties) {
		this.properties = properties;
	}
	public NodeBean merge(NodeBean otherBean) {
		this.properties.putAll(otherBean.getProperties());
		return this;
	}
	public <B extends NodeBean> B getAspect(B otherBean) {
		otherBean.setProperties(getProperties());
		return otherBean;
	}
	
	@SuppressWarnings("unchecked")
	public <C extends Serializable> C getProperty(SinglePropertyModel<C> property) {
		return (C) properties.get(property.getQName());
	}
	@SuppressWarnings("unchecked")
	public <C extends Serializable> List<C> getProperty(MultiPropertyModel<C> property) {
		return (List<C>) properties.get(property.getQName());
	}
	public <E extends Enum<E>> E getProperty(EnumTextPropertyModel<E> property) {
		return textToEnum(property, (String) properties.get(property.getQName()));
	}
	public static <E extends Enum<E>> E textToEnum(EnumTextPropertyModel<E> propertyModel, String code) {
		if (code == null) {
			return null;
		}
		
		Class<E> enumClass = propertyModel.getEnumClass();
		if (PropertyEnumeration.class.isAssignableFrom(enumClass)) {
			E otherValue = null;
			for (E e : enumClass.getEnumConstants()) {
				String enumCode = ((PropertyEnumeration) e).getCode();
				if (code.equals(enumCode)) {
					return e;
				}
				if (PropertyEnumeration.OTHER_VALUES.equals(enumCode)) {
					otherValue = e;
				}
			}
			if (otherValue != null) {
				return otherValue;
			}
		} else {
			for (E e : enumClass.getEnumConstants()) {
				if (code.equals(e.name())) {
					return e;
				}
			}
		}
		throw new IllegalStateException("Can't find value for '" + code + "' in enum " + enumClass.getName());
	}
	public static <E extends Enum<E>> String enumToText(E e) {
		if (e == null) {
			return null;
		}
		String code = (e instanceof PropertyEnumeration) 
				? ((PropertyEnumeration) e).getCode() 
				: e.name();
		if (PropertyEnumeration.OTHER_VALUES.equals(code)) {
			throw new IllegalStateException("You can't set back the OTHER_VALUES.");
		}
		return code;
	}
	
	public <C extends Serializable> void setProperty(SinglePropertyModel<C> property, C value) {
		properties.put(property.getQName(), value);
	}
	public <C extends Serializable> void setProperty(MultiPropertyModel<C> property, List<C> value) {
		properties.put(property.getQName(), (Serializable) value);
	}
	public <E extends Enum<E>> void setProperty(EnumTextPropertyModel<E> property, E value) {
		properties.put(property.getQName(), enumToText(value));
	}
	
	public void unsetProperty(PropertyModel<?> property) {
		properties.remove(property.getQName());
	}
}
