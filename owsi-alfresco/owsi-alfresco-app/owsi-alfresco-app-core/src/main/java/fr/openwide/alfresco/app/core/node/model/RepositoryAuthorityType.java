package fr.openwide.alfresco.app.core.node.model;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;

public class RepositoryAuthorityType extends AbstractStringUserType<RepositoryAuthority> {

	@Override
	public Class<RepositoryAuthority> returnedClass() {
		return RepositoryAuthority.class;
	}

	@Override
	protected RepositoryAuthority getAsObject(String value) {
		return RepositoryAuthority.authority(value);
	}

}
