package fr.openwide.alfresco.component.model.search.model;

import fr.openwide.alfresco.api.core.search.model.RepositorySortDefinition;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;


public class SortBuilder {

	private final SearchQueryBuilder searchBuilder;
	
	public SortBuilder(SearchQueryBuilder searchBuilder) {
		this.searchBuilder = searchBuilder;
	}
	
	public SearchQueryBuilder sortByTitle() {
		return asc(CmModel.titled.title);
	}
	public SearchQueryBuilder sortByName() {
		return asc(CmModel.object.name);
	}
	public SearchQueryBuilder sortByCreationTime() {
		return desc(CmModel.auditable.created);
	}
	public SearchQueryBuilder sortByModificationTime() {
		return desc(CmModel.auditable.modified);
	}
	public SearchQueryBuilder asc(SinglePropertyModel<? extends Comparable<?>> property) {
		return sort(property, true);
	}
	public SearchQueryBuilder desc(SinglePropertyModel<? extends Comparable<?>> property) {
		return sort(property, false);
	}
	public SearchQueryBuilder sort(SinglePropertyModel<? extends Comparable<?>> property, boolean ascending) {
		searchBuilder.getSearchParameters().getSorts().add(new RepositorySortDefinition(property.getNameReference(), ascending));
		return searchBuilder;
	}

}
