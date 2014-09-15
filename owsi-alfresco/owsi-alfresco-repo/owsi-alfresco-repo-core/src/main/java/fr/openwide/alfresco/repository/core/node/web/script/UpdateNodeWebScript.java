package fr.openwide.alfresco.repository.core.node.web.script;

import java.util.Collection;
import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService.UPDATE_NODE_SERVICE;

public class UpdateNodeWebScript extends AbstractNodeWebScript<Void, UPDATE_NODE_SERVICE> {

	@Override
	protected Void execute(UPDATE_NODE_SERVICE request) {
		nodeService.update(
				Objects.requireNonNull(request.nodes, "RepositoryNode"), 
				Objects.requireNonNull(request.nodeScope, "NodeScope"));
		
		return null;
	}

	@Override
	protected Collection<RepositoryNode> getInputNodes(UPDATE_NODE_SERVICE payload) {
		return payload.nodes;
	}
	
	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(UPDATE_NODE_SERVICE.class);
	}

}
