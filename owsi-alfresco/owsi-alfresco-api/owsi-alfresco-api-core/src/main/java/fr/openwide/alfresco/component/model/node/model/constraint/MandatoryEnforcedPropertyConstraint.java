package fr.openwide.alfresco.component.model.node.model.constraint;

import java.io.Serializable;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;

public class MandatoryEnforcedPropertyConstraint extends PropertyConstraint {

	public static final MandatoryEnforcedPropertyConstraint INSTANCE = new MandatoryEnforcedPropertyConstraint();
	
	protected MandatoryEnforcedPropertyConstraint() {}
	
	@Override
	public boolean valid(PropertyModel<?> propertyModel, Serializable value) {
		if (propertyModel.getConstraint(ProtectedPropertyConstraint.class) != null) {
			return true;
		}
		return valid(value);
	}
	
	@Override
	public boolean valid(Serializable value) {
		if (value != null) {
			if (value instanceof String) {
				return ((String) value).length() > 0;
			} else if (value instanceof Iterable) {
				return ((Iterable<?>) value).iterator().hasNext();
			}
			return true;
		}
		return false;
	}

	@Override
	public String getMessage() {
		return "Mandatory";
	}
}
