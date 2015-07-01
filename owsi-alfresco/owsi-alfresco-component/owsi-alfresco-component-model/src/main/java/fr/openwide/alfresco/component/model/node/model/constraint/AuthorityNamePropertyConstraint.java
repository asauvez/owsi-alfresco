package fr.openwide.alfresco.component.model.node.model.constraint;

import java.io.Serializable;
import java.util.List;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;

public class AuthorityNamePropertyConstraint extends PropertyConstraint {

	public static final AuthorityNamePropertyConstraint INSTANCE = new AuthorityNamePropertyConstraint();
	
	protected AuthorityNamePropertyConstraint() {}

	@Override
	public boolean valid(Serializable value) {
		if (value instanceof String) {
			return acceptString((String) value);
		} else if (value instanceof List) {
			for (Object o : ((List<?>) value)) {
				if (! acceptString((String) o)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private boolean acceptString(String value) {
		return value.startsWith(RepositoryAuthority.GROUP_PREFIX) || value.startsWith("ROLE_");
	}
}
