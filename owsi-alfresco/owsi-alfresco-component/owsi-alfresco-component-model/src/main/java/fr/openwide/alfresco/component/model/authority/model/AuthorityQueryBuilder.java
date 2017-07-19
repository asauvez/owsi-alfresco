package fr.openwide.alfresco.component.model.authority.model;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.authority.model.AuthorityQueryParameters;
import fr.openwide.alfresco.api.core.authority.model.AuthorityTypeReference;
import fr.openwide.alfresco.api.core.authority.model.ZoneReference;
import fr.openwide.alfresco.component.model.node.model.builder.AbstractQueryBuilder;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;


public class AuthorityQueryBuilder extends AbstractQueryBuilder<AuthorityQueryBuilder, AuthorityQueryParameters, AuthoritySortBuilder> {

	public AuthorityQueryBuilder() {
		super(new AuthorityQueryParameters(), new AuthoritySortBuilder());
	}
	
	public AuthorityQueryBuilder parentAuthority(AuthorityReference parentAuthority) {
		getParameters().setParentAuthority(parentAuthority);
		return this;
	}
	public AuthorityQueryBuilder parentAuthorityImmediate(AuthorityReference parentAuthority) {
		getParameters().setParentAuthority(parentAuthority);
		getParameters().setImmediate(true);
		return this;
	}
	public AuthorityQueryBuilder includingParent() {
		getParameters().setIncludingParent(true);
		return this;
	}

	public AuthorityQueryBuilder type(AuthorityTypeReference type) {
		getParameters().setAuthorityType(type);
		return this;
	}
	
	public AuthorityQueryBuilder zone(ZoneReference zone) {
		getParameters().setZone(zone);
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
