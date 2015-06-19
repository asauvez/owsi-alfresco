package fr.openwide.alfresco.component.model.search;

import fr.openwide.alfresco.api.core.remote.model.StoreReference;
import fr.openwide.alfresco.api.core.search.model.RepositorySearchParameters;
import fr.openwide.alfresco.api.core.search.model.RepositorySortDefinition;
import fr.openwide.alfresco.api.core.search.model.SearchQueryLanguage;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.search.restriction.RestrictionBuilder;


public class SearchBuilder {

	private RepositorySearchParameters searchParameters = new RepositorySearchParameters();
	
	public SearchBuilder restriction(RestrictionBuilder restrictionBuilder) {
		searchParameters.setQuery(restrictionBuilder.toQuery());
		searchParameters.setLanguage(SearchQueryLanguage.FTS_ALFRESCO);
		return this;
	}

	public SearchBuilder asc(SinglePropertyModel<?> property) {
		return addSort(property, true);
	}

	public SearchBuilder desc(SinglePropertyModel<?> property) {
		return addSort(property, false);
	}

	public SearchBuilder addSort(SinglePropertyModel<?> property, boolean ascending) {
		searchParameters.getSorts().add(new RepositorySortDefinition(property.getNameReference(), ascending));
		return this;
	}
	
	public SearchBuilder storeReference(StoreReference storeReference) {
		searchParameters.setStoreReference(storeReference);
		return this;
	}

	public SearchBuilder firstResult(Integer firstResult) {
		searchParameters.setFirstResult(firstResult);
		return this;
	}
	public SearchBuilder maxResults(Integer maxResults) {
		searchParameters.setFirstResult(maxResults);
		return this;
	}

	public RepositorySearchParameters getSearchParameters() {
		return searchParameters;
	}
}
