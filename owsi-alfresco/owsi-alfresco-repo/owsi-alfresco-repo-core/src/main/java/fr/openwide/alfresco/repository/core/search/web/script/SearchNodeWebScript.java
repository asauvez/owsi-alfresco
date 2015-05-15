package fr.openwide.alfresco.repository.core.search.web.script;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.node.model.RemoteCallParameters;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.search.service.NodeSearchRemoteService;
import fr.openwide.alfresco.api.core.search.service.NodeSearchRemoteService.SEARCH_NODE_SERVICE;
import fr.openwide.alfresco.repository.core.node.web.script.AbstractNodeListWebScript;

public class SearchNodeWebScript extends AbstractNodeListWebScript<SEARCH_NODE_SERVICE> {

	protected NodeSearchRemoteService nodeSearchService;

	@Override
	protected List<RepositoryNode> execute(SEARCH_NODE_SERVICE parameter) {
		return nodeSearchService.search(
				Objects.requireNonNull(parameter.query, "Query"), 
				Objects.requireNonNull(parameter.storeReference, "StoreReference"), 
				Objects.requireNonNull(parameter.nodeScope, "NodeScope"),
				Objects.requireNonNull(parameter.remoteCallParameters, "RemoteCallParameters"), 
				Objects.requireNonNull(parameter.language, "Language"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(SEARCH_NODE_SERVICE.class);
	}

	public void setNodeSearchService(NodeSearchRemoteService nodeSearchService) {
		this.nodeSearchService = nodeSearchService;
	}

	@Override
	protected RemoteCallParameters getRemoteCallParameters(SEARCH_NODE_SERVICE payload) {
		return payload.remoteCallParameters;
	}
}
