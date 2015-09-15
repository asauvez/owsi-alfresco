package fr.openwide.alfresco.component.model.authority.model;

import fr.openwide.alfresco.component.model.node.model.builder.AbstractSortBuilder;
import fr.openwide.alfresco.component.model.repository.model.CmModel;


public class AuthoritySortBuilder extends AbstractSortBuilder<AuthorityQueryBuilder> {

	// Pour les personnes
	public AuthorityQueryBuilder sortByLastName() {
		return asc(CmModel.person.lastName);
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

}
