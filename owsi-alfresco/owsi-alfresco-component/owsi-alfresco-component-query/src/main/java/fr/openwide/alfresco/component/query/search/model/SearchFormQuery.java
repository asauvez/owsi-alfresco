package fr.openwide.alfresco.component.query.search.model;

import fr.openwide.alfresco.component.model.search.restriction.RestrictionBuilder;

public abstract class SearchFormQuery extends NodeFormQuery {

	public abstract void initRestrictions(RestrictionBuilder restrictionBuilder);

}
