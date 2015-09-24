package fr.openwide.alfresco.component.model.node.model.property;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.Model;
import fr.openwide.alfresco.component.model.node.model.constraint.ConstraintException;
import fr.openwide.alfresco.component.model.node.model.constraint.PropertyConstraint;
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

}
