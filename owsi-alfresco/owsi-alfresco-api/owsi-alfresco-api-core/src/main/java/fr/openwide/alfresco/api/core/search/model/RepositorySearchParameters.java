package fr.openwide.alfresco.api.core.search.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.openwide.alfresco.api.core.node.model.AbstractQueryParameters;
import fr.openwide.alfresco.api.core.remote.model.StoreReference;

public class RepositorySearchParameters extends AbstractQueryParameters {

	private String query;
	private List<StoreReference> storeReferences = new ArrayList<>(Arrays.asList(StoreReference.STORE_REF_WORKSPACE_SPACESSTORE));
	private SearchQueryLanguage language = SearchQueryLanguage.FTS_ALFRESCO;
	private RepositoryQueryConsistency queryConsistency = RepositoryQueryConsistency.DEFAULT;
	
	private Integer firstResult;
	private Integer maxResults;
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public List<StoreReference> getStoreReferences() {
		return storeReferences;
	}
	public void setStoreReferences(List<StoreReference> storeReferences) {
		this.storeReferences = storeReferences;
	}
	
	public SearchQueryLanguage getLanguage() {
		return language;
	}
	public void setLanguage(SearchQueryLanguage language) {
		this.language = language;
	}

	public Integer getFirstResult() {
		return firstResult;
	}
	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}
	
	public Integer getMaxResults() {
		return maxResults;
	}
	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}

	public RepositoryQueryConsistency getQueryConsistency() {
		return queryConsistency;
	}
	public void setQueryConsistency(RepositoryQueryConsistency queryConsistency) {
		this.queryConsistency = queryConsistency;
	}

}
