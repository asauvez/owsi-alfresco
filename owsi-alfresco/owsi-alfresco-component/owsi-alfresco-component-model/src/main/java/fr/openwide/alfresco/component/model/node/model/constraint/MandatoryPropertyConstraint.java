package fr.openwide.alfresco.component.model.node.model.constraint;

import java.io.Serializable;

public class MandatoryPropertyConstraint extends PropertyConstraint {

	public static final MandatoryPropertyConstraint INSTANCE = new MandatoryPropertyConstraint();
	
	protected MandatoryPropertyConstraint() {}
	
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
