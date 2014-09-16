package fr.openwide.alfresco.component.model.node.model.constraint;

import java.io.Serializable;

public abstract class PropertyConstraint {

	public abstract boolean valid(Serializable value);
	
	public String getMessage() {
		return this.getClass().getSimpleName();
	}
	
}
