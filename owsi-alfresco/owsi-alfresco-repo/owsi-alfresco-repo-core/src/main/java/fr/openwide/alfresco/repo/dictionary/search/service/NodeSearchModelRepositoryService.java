package fr.openwide.alfresco.repo.dictionary.search.service;

import java.util.List;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.search.model.SearchQueryBuilder;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;
import fr.openwide.alfresco.repo.dictionary.search.model.BatchSearchQueryBuilder;

public interface NodeSearchModelRepositoryService extends NodeSearchModelService {

	List<NodeReference> searchReference(RestrictionBuilder restrictionBuilder);
	List<NodeReference> searchReference(SearchQueryBuilder searchBuilder);

	int searchBatch(BatchSearchQueryBuilder searchBuilder);
}
