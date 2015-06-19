package fr.openwide.alfresco.app.core.search.service;

import java.util.List;

import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.search.service.NodeSearchRemoteService;

public interface NodeSearchService extends NodeSearchRemoteService {

	List<RepositoryNode> search(String query, NodeScope nodeScope);
	
}
