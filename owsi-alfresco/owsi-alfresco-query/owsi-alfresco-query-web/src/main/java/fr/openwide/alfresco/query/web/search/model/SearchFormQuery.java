package fr.openwide.alfresco.query.web.search.model;

import fr.openwide.alfresco.query.core.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.query.core.search.restriction.RestrictionBuilder.LogicalOperator;

public abstract class SearchFormQuery extends NodeFormQuery {

	private RestrictionBuilder restrictionBuilder = new RestrictionBuilder(null, LogicalOperator.AND);

	public abstract void initRestrictions(RestrictionBuilder restrictionBuilder);

	public RestrictionBuilder getRestrictionBuilder() {
		return restrictionBuilder;
	}
}
