package fr.openwide.alfresco.component.model.node.model.property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.Model;
import fr.openwide.alfresco.component.model.node.model.constraint.ConstraintException;
import fr.openwide.alfresco.component.model.node.model.constraint.PropertyConstraint;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public abstract class PropertyModel<C extends Serializable> extends Model {

	private final ContainerModel type;
	private final List<PropertyConstraint> constraints = new ArrayList<>(); 

	public PropertyModel(ContainerModel type, NameReference nameReference) {
		super(nameReference);
		this.type = type;
		type.addProperty(this);
	}
	
	public abstract Class<C> getValueClass();

	public ContainerModel getType() {
		return type;
	}

	public abstract String getDataType();
	
	public PropertyModel<C> add(PropertyConstraint constraint) {
		constraints.add(constraint);
		return this;
	}
	
	public void validate(Serializable value) {
		validateType(value);
		for (PropertyConstraint constraint : constraints) {
			if (! constraint.valid(this, value)) {
				throw new ConstraintException(getNameReference() + ": Value does not satisfy constraint " + constraint.getMessage());
			}
		}
	}

	protected void validateType(Serializable value) {
		if (value != null && ! getValueClass().isInstance(value)) {
			throw new ConstraintException(getNameReference() + ": Value of type " + value.getClass().getName() + " instead of " + getValueClass().getName() + ".");
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends PropertyConstraint> T getConstraint(Class<T> clazz) {
		for (PropertyConstraint constraint : constraints) {
			if (clazz.isAssignableFrom(constraint.getClass())) {
				return (T) constraint;
			}
		}
		return null;
	}
	
	public String getXmlModel() {
		return getXmlModel(0);
	}

	public String getXmlModel(int profondeur) {
		StringBuilder xml = new StringBuilder();
		StringBuilder tabulation = new StringBuilder();
		
		for (int i = 0; i < profondeur; i++){
			tabulation.append("	");
		}
		
		xml.append(tabulation.toString()).append("<property name=\"") .append(this.getNameReference().getFullName()).append("\">\n")
			.append(tabulation.toString()).append("	<type>").append(this.getDataType()).append("</type>\n");
		
		if (this instanceof MultiPropertyModel) {
			xml.append(tabulation.toString()).append("	<multiple>true</multiple>\n");
		}
		
		xml.append(tabulation.toString()).append("</property>\n");
		
		return xml.toString();
	}
}
