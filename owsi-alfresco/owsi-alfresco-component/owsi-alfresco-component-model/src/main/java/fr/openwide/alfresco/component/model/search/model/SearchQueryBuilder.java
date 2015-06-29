package fr.openwide.alfresco.component.model.search.model;

import fr.openwide.alfresco.api.core.remote.model.StoreReference;
import fr.openwide.alfresco.api.core.search.model.RepositorySearchParameters;
import fr.openwide.alfresco.api.core.search.model.SearchQueryLanguage;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;


public class SearchQueryBuilder {

	private RepositorySearchParameters searchParameters = new RepositorySearchParameters();
	private SortBuilder sortBuilder = new SortBuilder(this);
	
	public SearchQueryBuilder restriction(RestrictionBuilder restrictionBuilder) {
		searchParameters.setQuery(restrictionBuilder.toQuery());
		searchParameters.setLanguage(SearchQueryLanguage.FTS_ALFRESCO);
		return this;
	}

	public SearchQueryBuilder nodeScopeBuilder(NodeScopeBuilder nodeScopeBuilder) {
		this.searchParameters.setNodeScope(nodeScopeBuilder.getScope());
		return this;
	}
	
	public SearchQueryBuilder storeReference(StoreReference storeReference) {
		searchParameters.setStoreReference(storeReference);
		return this;
	}

	/** @see org.hibernate.Query#setFirstResult(int) */
	public SearchQueryBuilder firstResult(Integer firstResult) {
		searchParameters.setFirstResult(firstResult);
		return this;
	}
	/** @see org.hibernate.Query#setMaxResults(int) */
	public SearchQueryBuilder maxResults(Integer maxResults) {
		searchParameters.setMaxResults(maxResults);
		return this;
	}

	public SortBuilder sort() {
		return sortBuilder;
	}
	
	public RepositorySearchParameters getSearchParameters() {
		return searchParameters;
	}

}
