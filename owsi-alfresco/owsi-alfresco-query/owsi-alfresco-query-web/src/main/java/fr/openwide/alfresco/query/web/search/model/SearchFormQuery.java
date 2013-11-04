package fr.openwide.alfresco.query.web.search.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.query.core.search.model.NodeResult;
import fr.openwide.alfresco.query.core.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.query.core.search.restriction.RestrictionBuilder.LogicalOperator;
import fr.openwide.alfresco.query.core.search.service.NodeSearchService;

public abstract class SearchFormQuery extends NodeFormQuery {

	@Autowired
	private NodeSearchService searchService;

	public abstract void initRestrictions(RestrictionBuilder restriction);

	@Override
	protected List<NodeResult> retrieveResults() {
		RestrictionBuilder restriction = new RestrictionBuilder(null, LogicalOperator.AND);
		initRestrictions(restriction);
		return searchService.search(restriction.toLuceneQuery());
	}

}
