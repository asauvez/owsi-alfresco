package fr.openwide.alfresco.component.model.authority.model;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthoritySearchParameters;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;


public class AuthorityQueryBuilder {

	private RepositoryAuthoritySearchParameters searchParameters = new RepositoryAuthoritySearchParameters();
	
	public AuthorityQueryBuilder parentAuthority(RepositoryAuthority parentAuthority) {
		this.searchParameters.setParentAuthority(parentAuthority);
		return this;
	}
	public AuthorityQueryBuilder parentAuthorityImmediate(RepositoryAuthority parentAuthority) {
		this.searchParameters.setParentAuthority(parentAuthority);
		this.searchParameters.setImmediate(true);
		return this;
	}
	
	public AuthorityQueryBuilder filterProperty(PropertyModel<String> filterProperty) {
		this.searchParameters.setFilterProperty(filterProperty.getNameReference());
		return this;
	}
	public AuthorityQueryBuilder filterValue(String filterValue) {
		this.searchParameters.setFilterValue(filterValue);
		return this;
	}
	
	public AuthorityQueryBuilder nodeScopeBuilder(NodeScopeBuilder nodeScopeBuilder) {
		this.searchParameters.setNodeScope(nodeScopeBuilder.getScope());
		return this;
	}
	
	public RepositoryAuthoritySearchParameters getSearchParameters() {
		return searchParameters;
	}

}
