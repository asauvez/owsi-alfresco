package fr.openwide.alfresco.api.core.search.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfresco.service.cmr.repository.StoreRef;

import fr.openwide.alfresco.api.core.node.model.AbstractQueryParameters;
import fr.openwide.alfresco.api.core.search.model.highlight.RepositoryGeneralHighlightParameters;

public class RepositorySearchParameters extends AbstractQueryParameters {

	private String query;
	private List<StoreRef> storeRefs = new ArrayList<>(Arrays.asList(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE));
	private SearchQueryLanguage language = SearchQueryLanguage.FTS_ALFRESCO;
	private RepositoryQueryConsistency queryConsistency = RepositoryQueryConsistency.DEFAULT;
	
	private RepositoryGeneralHighlightParameters highlight;
	
	private Integer firstResult;
	private Integer maxResults;
	private Integer maxPermissionChecks;
	private Long maxPermissionCheckTimeMillis;

	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public List<StoreRef> getStoreRefs() {
		return storeRefs;
	}
	public void setStoreRefs(List<StoreRef> storeRefs) {
		this.storeRefs = storeRefs;
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
	
	public Integer getMaxPermissionChecks() {
		return maxPermissionChecks;
	}
	public void setMaxPermissionChecks(Integer maxPermissionChecks) {
		this.maxPermissionChecks = maxPermissionChecks;
	}
	
	public Long getMaxPermissionCheckTimeMillis() {
		return maxPermissionCheckTimeMillis;
	}
	public void setMaxPermissionCheckTimeMillis(Long maxPermissionCheckTimeMillis) {
		this.maxPermissionCheckTimeMillis = maxPermissionCheckTimeMillis;
	}

	public RepositoryQueryConsistency getQueryConsistency() {
		return queryConsistency;
	}
	public void setQueryConsistency(RepositoryQueryConsistency queryConsistency) {
		this.queryConsistency = queryConsistency;
	}

	public RepositoryGeneralHighlightParameters getHighlight() {
		return highlight;
	}
	public void setHighlight(RepositoryGeneralHighlightParameters highlight) {
		this.highlight = highlight;
	}
}
