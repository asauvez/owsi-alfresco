package fr.openwide.alfresco.repository.core.node.web.script;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService.GET_NODE_SERVICE;

public class GetNodeWebScript extends AbstractNodeWebScript<RepositoryNode, GET_NODE_SERVICE> {

	@Override
	protected RepositoryNode execute(GET_NODE_SERVICE payload) {
		return nodeService.get(
				Objects.requireNonNull(payload.nodeReference, "NodeReference"), 
				Objects.requireNonNull(payload.nodeScope, "NodeScope"));
	}

	@Override
	protected Collection<RepositoryNode> getOutputNodes(RepositoryNode result) {
		return Collections.singleton(result);
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(GET_NODE_SERVICE.class);
	}

}
