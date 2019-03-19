package fr.openwide.alfresco.repo.dictionary.search.service;

import java.util.List;

import org.alfresco.service.cmr.repository.NodeRef;

import fr.openwide.alfresco.component.model.search.model.SearchQueryBuilder;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;
import fr.openwide.alfresco.repo.dictionary.search.model.BatchSearchQueryBuilder;

public interface NodeSearchModelRepositoryService extends NodeSearchModelService {

	List<NodeRef> searchReference(RestrictionBuilder restrictionBuilder);
	List<NodeRef> searchReference(SearchQueryBuilder searchBuilder);

	int searchBatch(BatchSearchQueryBuilder searchBuilder);
}
