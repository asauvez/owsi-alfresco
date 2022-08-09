package fr.openwide.alfresco.component.model.search.model;

import fr.openwide.alfresco.component.model.node.model.builder.AbstractSortBuilder;
import fr.openwide.alfresco.component.model.repository.model.CmModel;


public class SearchSortBuilder extends AbstractSortBuilder<SearchQueryBuilder> {

	public SearchQueryBuilder sortByTitle() {
		return asc(CmModel.titled.title);
	}
	public SearchQueryBuilder sortByName() {
		return asc(CmModel.cmobject.name);
	}
	public SearchQueryBuilder sortByCreationTime() {
		return desc(CmModel.auditable.created);
	}
	public SearchQueryBuilder sortByModificationTime() {
		return desc(CmModel.auditable.modified);
	}

}
