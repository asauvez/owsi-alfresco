package fr.openwide.alfresco.component.model.authority.model;

import fr.openwide.alfresco.api.core.search.model.RepositorySortDefinition;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;


public class AuthoritySortBuilder {

	private final AuthorityQueryBuilder queryBuilder;
	
	public AuthoritySortBuilder(AuthorityQueryBuilder queryBuilder) {
		this.queryBuilder = queryBuilder;
	}
	
	// Pour les personnes
	public AuthorityQueryBuilder sortByLastName() {
		return asc(CmModel.person.lastName);
	}
	public AuthorityQueryBuilder sortByMiddleName() {
		return asc(CmModel.person.middleName);
	}
	public AuthorityQueryBuilder sortByFirstName() {
		return asc(CmModel.person.firstName);
	}
	public AuthorityQueryBuilder sortByUserName() {
		return asc(CmModel.person.userName);
	}
	public AuthorityQueryBuilder sortByEmail() {
		return asc(CmModel.person.email);
	}

	// Pour les groupes
	public AuthorityQueryBuilder sortByAuthorityDisplayName() {
		return asc(CmModel.authorityContainer.authorityDisplayName);
	}
	public AuthorityQueryBuilder sortByAuthorityName() {
		return asc(CmModel.authorityContainer.authorityName);
	}

	public AuthorityQueryBuilder asc(SinglePropertyModel<? extends Comparable<?>> property) {
		return sort(property, true);
	}
	public AuthorityQueryBuilder desc(SinglePropertyModel<? extends Comparable<?>> property) {
		return sort(property, false);
	}
	public AuthorityQueryBuilder sort(SinglePropertyModel<? extends Comparable<?>> property, boolean ascending) {
		queryBuilder.getSearchParameters().getSorts().add(new RepositorySortDefinition(property.getNameReference(), ascending));
		return queryBuilder;
	}

}
