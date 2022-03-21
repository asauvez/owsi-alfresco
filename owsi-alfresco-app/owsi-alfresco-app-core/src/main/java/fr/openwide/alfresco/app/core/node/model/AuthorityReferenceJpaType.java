package fr.openwide.alfresco.app.core.node.model;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;

public class AuthorityReferenceJpaType extends AbstractStringUserType<AuthorityReference> {

	@Override
	public Class<AuthorityReference> returnedClass() {
		return AuthorityReference.class;
	}

	@Override
	protected AuthorityReference getAsObject(String value) {
		return AuthorityReference.authority(value);
	}

}
