package fr.openwide.alfresco.repository.core.node.web.script;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService.GET_NODE_SERVICE;

public class GetNodeWebScript extends AbstractNodeWebScript<RepositoryNode, GET_NODE_SERVICE> {

	@Override
	protected RepositoryNode execute(GET_NODE_SERVICE parameter) {
		return nodeService.get(
				Objects.requireNonNull(parameter.nodeReference, "NodeReference"), 
				Objects.requireNonNull(parameter.nodeScope, "NodeScope"));
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
