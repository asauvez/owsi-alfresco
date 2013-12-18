package fr.openwide.alfresco.query.core.search.service;

import java.util.List;

import fr.openwide.alfresco.query.core.search.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.search.service.NodeSearchRemoteService;

public interface NodeSearchService extends NodeSearchRemoteService {

	List<RepositoryNode> search(RestrictionBuilder builder, NodeFetchDetails nodeFetchDetails);
}
