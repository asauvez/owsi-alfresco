package fr.openwide.alfresco.component.model.search.model;

import java.util.List;

import fr.openwide.alfresco.api.core.remote.model.StoreReference;
import fr.openwide.alfresco.api.core.search.model.RepositoryQueryConsistency;
import fr.openwide.alfresco.api.core.search.model.RepositorySearchParameters;
import fr.openwide.alfresco.api.core.search.model.SearchQueryLanguage;
import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.node.model.builder.AbstractQueryBuilder;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;


public class SearchQueryBuilder extends AbstractQueryBuilder<SearchQueryBuilder, RepositorySearchParameters, SearchSortBuilder> {

	public SearchQueryBuilder() {
		super(new RepositorySearchParameters(), new SearchSortBuilder());
	}
	
	public SearchQueryBuilder restriction(RestrictionBuilder restrictionBuilder) {
		getParameters().setQuery(restrictionBuilder.toFtsQuery());
		getParameters().setLanguage(SearchQueryLanguage.FTS_ALFRESCO);
		return this;
	}
	
	public SearchQueryBuilder restrictionCmisContent(RestrictionBuilder restrictionBuilder) {
		return restrictionCmis(restrictionBuilder, CmModel.content);
	}
	
	public SearchQueryBuilder restrictionCmisFolder(RestrictionBuilder restrictionBuilder) {
		return restrictionCmis(restrictionBuilder, CmModel.folder);
	}
	
	public SearchQueryBuilder restrictionCmis(RestrictionBuilder restrictionBuilder, TypeModel type) {
		getParameters().setQuery(restrictionBuilder.toCmisQuery(type));
		getParameters().setLanguage(SearchQueryLanguage.CMIS_ALFRESCO);
		return this;
	}
	
	
	public SearchQueryBuilder storeReferences(List<StoreReference> storeReferences) {
		getParameters().setStoreReferences(storeReferences);
		return this;
	}

	/** @see org.hibernate.Query#setFirstResult(int) */
	public SearchQueryBuilder firstResult(Integer firstResult) {
		getParameters().setFirstResult(firstResult);
		return this;
	}
	/** @see org.hibernate.Query#setMaxResults(int) */
	public SearchQueryBuilder maxResults(Integer maxResults) {
		getParameters().setMaxResults(maxResults);
		return this;
	}

	public SearchQueryBuilder queryConsistency(RepositoryQueryConsistency queryConsistency) {
		getParameters().setQueryConsistency(queryConsistency);
		return this;
	}
	
}
