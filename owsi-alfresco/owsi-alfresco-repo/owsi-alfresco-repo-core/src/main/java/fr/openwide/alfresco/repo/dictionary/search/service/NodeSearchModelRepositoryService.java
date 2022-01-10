package fr.openwide.alfresco.repo.dictionary.search.service;

import java.util.List;
import java.util.Optional;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchParameters;

import fr.openwide.alfresco.api.core.search.model.RepositorySearchParameters;
import fr.openwide.alfresco.component.model.search.model.SearchQueryBuilder;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repo.dictionary.search.model.BatchSearchQueryBuilder;

public interface NodeSearchModelRepositoryService {

	List<NodeRef> searchReference(RestrictionBuilder restrictionBuilder);
	Optional<NodeRef> searchReferenceUnique(RestrictionBuilder restrictionBuilder);
	NodeRef searchReferenceMandatory(RestrictionBuilder restrictionBuilder);
	List<NodeRef> searchReference(SearchQueryBuilder searchBuilder);

	int searchBatch(BatchSearchQueryBuilder searchBuilder);
	
	SearchParameters getSearchParameters(RepositorySearchParameters rsp);
}
