package fr.openwide.alfresco.api.core.search.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.openwide.alfresco.api.core.remote.model.StoreReference;

public class RepositorySearchParameters implements Serializable {

	private String query;
	private StoreReference storeReference = StoreReference.STORE_REF_WORKSPACE_SPACESSTORE;
	private SearchQueryLanguage language = SearchQueryLanguage.FTS_ALFRESCO;
	
	private Integer firstResult;
	private Integer maxResults;
	
	private List<RepositorySortDefinition> sorts = new ArrayList<>();
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	
	public StoreReference getStoreReference() {
		return storeReference;
	}
	public void setStoreReference(StoreReference storeReference) {
		this.storeReference = storeReference;
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
	
	public List<RepositorySortDefinition> getSorts() {
		return sorts;
	}
}
