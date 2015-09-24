package fr.openwide.alfresco.component.model.authority.model;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthorityQueryParameters;
import fr.openwide.alfresco.component.model.node.model.builder.AbstractQueryBuilder;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;


public class AuthorityQueryBuilder extends AbstractQueryBuilder<AuthorityQueryBuilder, RepositoryAuthorityQueryParameters, AuthoritySortBuilder> {

	public AuthorityQueryBuilder() {
		super(new RepositoryAuthorityQueryParameters(), new AuthoritySortBuilder());
	}
	
	public AuthorityQueryBuilder parentAuthority(RepositoryAuthority parentAuthority) {
		getParameters().setParentAuthority(parentAuthority);
		return this;
	}
	public AuthorityQueryBuilder parentAuthorityImmediate(RepositoryAuthority parentAuthority) {
		getParameters().setParentAuthority(parentAuthority);
		getParameters().setImmediate(true);
		return this;
	}

	public AuthorityQueryBuilder filterProperty(PropertyModel<String> filterProperty) {
		getParameters().setFilterProperty(filterProperty.getNameReference());
		return this;
	}
	public AuthorityQueryBuilder filterValue(String filterValue) {
		getParameters().setFilterValue(filterValue);
		return this;
	}

}
