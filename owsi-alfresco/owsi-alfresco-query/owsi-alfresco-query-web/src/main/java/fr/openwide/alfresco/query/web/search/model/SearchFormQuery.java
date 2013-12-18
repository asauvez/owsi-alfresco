package fr.openwide.alfresco.query.web.search.model;

import fr.openwide.alfresco.query.core.search.restriction.RestrictionBuilder;

public abstract class SearchFormQuery extends NodeFormQuery {

	public abstract void initRestrictions(RestrictionBuilder restrictionBuilder);

}
