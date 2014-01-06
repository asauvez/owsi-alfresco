package fr.openwide.alfresco.repository.core.search.web.script;

import java.util.List;
import java.util.Objects;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.repository.api.search.service.NodeSearchRemoteService;
import fr.openwide.alfresco.repository.api.search.service.NodeSearchRemoteService.SEARCH_NODE_SERVICE;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractMessageRemoteWebScript;

public class SearchNodeWebScript extends AbstractMessageRemoteWebScript<List<RepositoryNode>, SEARCH_NODE_SERVICE> {

	protected NodeSearchRemoteService nodeSearchService;

	@Override
	protected List<RepositoryNode> executeImpl(SEARCH_NODE_SERVICE parameter, Status status, Cache cache) throws RepositoryRemoteException {
		return nodeSearchService.search(
				Objects.requireNonNull(parameter.query, "Query"), 
				Objects.requireNonNull(parameter.storeReference, "StoreReference"), 
				Objects.requireNonNull(parameter.nodeFetchDetails, "NodeFetchDetails"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(SEARCH_NODE_SERVICE.class);
	}

	public void setNodeSearchService(NodeSearchRemoteService nodeSearchService) {
		this.nodeSearchService = nodeSearchService;
	}

}
