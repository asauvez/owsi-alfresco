package fr.openwide.alfresco.component.model.node.model.constraint;

import java.io.Serializable;

/**
 * La valeur ne doit pas être affecter par l'utilisateur. Seul Alfresco peut affecter une valeur.
 */
public class ProtectedPropertyConstraint extends PropertyConstraint {

	public static final ProtectedPropertyConstraint INSTANCE = new ProtectedPropertyConstraint();
	
	protected ProtectedPropertyConstraint() {}
	
	@Override
	public boolean valid(Serializable value) {
		return (value == null);
	}

	@Override
	public String getMessage() {
		return "Protected";
	}
}
