package fr.openwide.alfresco.component.model.node.model.constraint;

import java.io.Serializable;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;

public abstract class PropertyConstraint {

	public boolean valid(
			@SuppressWarnings("unused") PropertyModel<?> propertyModel, 
			Serializable value) {
		return valid(value);
	}

	public abstract boolean valid(Serializable value);

	public String getMessage() {
		return this.getClass().getSimpleName();
	}

}
