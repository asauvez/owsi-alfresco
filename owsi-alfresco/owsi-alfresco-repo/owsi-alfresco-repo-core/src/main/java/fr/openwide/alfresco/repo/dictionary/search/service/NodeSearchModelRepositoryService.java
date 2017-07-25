package fr.openwide.alfresco.repo.dictionary.search.service;

import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;
import fr.openwide.alfresco.repo.dictionary.search.model.BatchSearchQueryBuilder;

public interface NodeSearchModelRepositoryService extends NodeSearchModelService {
	
	int searchBatch(BatchSearchQueryBuilder searchBuilder);
}
