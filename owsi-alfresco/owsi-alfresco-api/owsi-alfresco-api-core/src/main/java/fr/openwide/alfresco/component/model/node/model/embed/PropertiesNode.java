package fr.openwide.alfresco.component.model.node.model.embed;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.property.PropertyEnumeration;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class PropertiesNode {

	private final BusinessNode node;
	private final RepositoryNode repoNode;
	
	public PropertiesNode(BusinessNode node) {
		this.node = node;
		this.repoNode = node.getRepositoryNode();
	}
	
	public String getName() {
		return get(CmModel.object.name);
	}
	public BusinessNode name(String name) {
		return set(CmModel.object.name, name);
	}

	public String getTitle() {
		return get(CmModel.titled.title);
	}
	public BusinessNode title(String name) {
		return set(CmModel.titled.title, name);
	}
	
	public String getDescription() {
		return get(CmModel.titled.description);
	}
	public BusinessNode description(String name) {
		return set(CmModel.titled.description, name);
	}
	
	public String getCreator() {
		return get(CmModel.auditable.creator);
	}
	public Date getCreated() {
		return get(CmModel.auditable.created);
	}
	public String getModifier() {
		return get(CmModel.auditable.modifier);
	}
	public Date getModified() {
		return get(CmModel.auditable.modified);
	}
	
	@SuppressWarnings("unchecked")
	public <C extends Serializable> C get(SinglePropertyModel<C> propertyModel) {
		return (C) repoNode.getProperty(propertyModel.getNameReference());
	}
	public <C extends Serializable> BusinessNode set(SinglePropertyModel<C> propertyModel, C value) {
		repoNode.getProperties().put(propertyModel.getNameReference(), value);
		return node;
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
	public <E extends Enum<E>> E get(EnumTextPropertyModel<E> propertyModel) {
		String code = (String) repoNode.getProperty(propertyModel.getNameReference());
		return textToEnum(propertyModel, code);
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
	public <E extends Enum<E>> BusinessNode set(EnumTextPropertyModel<E> propertyModel, E e) {
		String code = enumToText(e);
		return set(propertyModel, code);
	}

	@SuppressWarnings("unchecked")
	public <C extends Serializable> List<C> get(MultiPropertyModel<C> propertyModel) {
		return (List<C>) repoNode.getProperty(propertyModel.getNameReference());
	}
	public <C extends Serializable> BusinessNode set(MultiPropertyModel<C> propertyModel, Collection<C> value) {
		repoNode.getProperties().put(propertyModel.getNameReference(), (Serializable) value); 
		return node;
	}
	public <C extends Serializable> BusinessNode set(MultiPropertyModel<C> propertyModel, @SuppressWarnings("unchecked") C ... values) {
		return set(propertyModel, Arrays.asList(values));
	}

}
