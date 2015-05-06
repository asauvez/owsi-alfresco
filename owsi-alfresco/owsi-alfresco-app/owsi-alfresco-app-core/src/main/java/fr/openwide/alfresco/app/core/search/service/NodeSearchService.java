package fr.openwide.alfresco.app.core.search.service;

import java.util.List;

import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RemoteCallParameters;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.StoreReference;
import fr.openwide.alfresco.repository.api.search.service.NodeSearchRemoteService;

public interface NodeSearchService extends NodeSearchRemoteService {

	List<RepositoryNode> search(String query, StoreReference storeReference, NodeScope nodeScope, RemoteCallParameters remoteCallParameters);
	
}
