package fr.openwide.alfresco.component.model.search;

import fr.openwide.alfresco.api.core.remote.model.StoreReference;
import fr.openwide.alfresco.api.core.search.model.RepositorySearchParameters;
import fr.openwide.alfresco.api.core.search.model.RepositorySortDefinition;
import fr.openwide.alfresco.api.core.search.model.SearchQueryLanguage;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.search.restriction.RestrictionBuilder;


public class SearchBuilder {

	private RepositorySearchParameters searchParameters = new RepositorySearchParameters();
	
	public SearchBuilder restriction(RestrictionBuilder restrictionBuilder) {
		searchParameters.setQuery(restrictionBuilder.toQuery());
		searchParameters.setLanguage(SearchQueryLanguage.FTS_ALFRESCO);
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

	public SearchBuilder sortByTitle() {
		return sortAsc(CmModel.titled.title);
	}
	public SearchBuilder sortByName() {
		return sortAsc(CmModel.object.name);
	}
	public SearchBuilder sortByCreationTime() {
		return sortDesc(CmModel.auditable.created);
	}
	public SearchBuilder sortByModificationTime() {
		return sortDesc(CmModel.auditable.modified);
	}
	public SearchBuilder sortAsc(SinglePropertyModel<? extends Comparable<?>> property) {
		return sort(property, true);
	}
	public SearchBuilder sortDesc(SinglePropertyModel<? extends Comparable<?>> property) {
		return sort(property, false);
	}
	public SearchBuilder sort(SinglePropertyModel<? extends Comparable<?>> property, boolean ascending) {
		searchParameters.getSorts().add(new RepositorySortDefinition(property.getNameReference(), ascending));
		return this;
	}

	public RepositorySearchParameters getSearchParameters() {
		return searchParameters;
	}

}
