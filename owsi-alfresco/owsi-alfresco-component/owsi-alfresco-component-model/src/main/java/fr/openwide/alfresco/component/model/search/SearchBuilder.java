package fr.openwide.alfresco.component.model.search;

import fr.openwide.alfresco.api.core.remote.model.StoreReference;
import fr.openwide.alfresco.api.core.search.model.RepositorySearchParameters;
import fr.openwide.alfresco.api.core.search.model.SearchQueryLanguage;
import fr.openwide.alfresco.component.model.search.restriction.RestrictionBuilder;


public class SearchBuilder {

	private RepositorySearchParameters searchParameters = new RepositorySearchParameters();
	private SortBuilder sortBuilder = new SortBuilder(this);
	
	public SearchBuilder restriction(RestrictionBuilder restrictionBuilder) {
		searchParameters.setQuery(restrictionBuilder.toQuery());
		searchParameters.setLanguage(SearchQueryLanguage.FTS_ALFRESCO);
		return this;
	}

	public SearchBuilder storeReference(StoreReference storeReference) {
		searchParameters.setStoreReference(storeReference);
		return this;
	}

	/** @see org.hibernate.Query#setFirstResult(int) */
	public SearchBuilder firstResult(Integer firstResult) {
		searchParameters.setFirstResult(firstResult);
		return this;
	}
	/** @see org.hibernate.Query#setMaxResults(int) */
	public SearchBuilder maxResults(Integer maxResults) {
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
