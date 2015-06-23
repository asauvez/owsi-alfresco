package fr.openwide.alfresco.component.model.search;

import fr.openwide.alfresco.api.core.search.model.RepositorySortDefinition;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;


public class SortBuilder {

	private final SearchBuilder searchBuilder;
	
	public SortBuilder(SearchBuilder searchBuilder) {
		this.searchBuilder = searchBuilder;
	}
	
	public SearchBuilder sortByTitle() {
		return asc(CmModel.titled.title);
	}
	public SearchBuilder sortByName() {
		return asc(CmModel.object.name);
	}
	public SearchBuilder sortByCreationTime() {
		return desc(CmModel.auditable.created);
	}
	public SearchBuilder sortByModificationTime() {
		return desc(CmModel.auditable.modified);
	}
	public SearchBuilder asc(SinglePropertyModel<? extends Comparable<?>> property) {
		return sort(property, true);
	}
	public SearchBuilder desc(SinglePropertyModel<? extends Comparable<?>> property) {
		return sort(property, false);
	}
	public SearchBuilder sort(SinglePropertyModel<? extends Comparable<?>> property, boolean ascending) {
		searchBuilder.getSearchParameters().getSorts().add(new RepositorySortDefinition(property.getNameReference(), ascending));
		return searchBuilder;
	}

}
