package fr.openwide.alfresco.app.core.search.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.app.core.search.service.NodeSearchService;
import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.StoreReference;
import fr.openwide.alfresco.repository.api.search.service.SearchQueryLanguage;

@Service
public class NodeSearchServiceImpl implements NodeSearchService {

	@Autowired
	private NodeService nodeService;

	@Override
	public List<RepositoryNode> search(String query, StoreReference storeReference, NodeScope nodeScope) {
		return search(query, storeReference, nodeScope, SearchQueryLanguage.FTS_ALFRESCO);
	}

	@Override
	public List<RepositoryNode> search(String query, StoreReference storeReference, NodeScope nodeScope, SearchQueryLanguage language) {
		SEARCH_NODE_SERVICE payload = new SEARCH_NODE_SERVICE();
		payload.query = query;
		payload.storeReference = storeReference;
		payload.nodeScope = nodeScope;
		payload.language = language;
		return nodeService.callNodeListSerializer(SEARCH_NODE_SERVICE.ENDPOINT, payload, nodeScope);
	}

}
