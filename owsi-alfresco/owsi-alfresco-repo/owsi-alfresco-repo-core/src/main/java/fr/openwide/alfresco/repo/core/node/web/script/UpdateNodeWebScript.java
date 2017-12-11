package fr.openwide.alfresco.repo.core.node.web.script;

import java.util.Collection;
import java.util.Objects;

import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService.UPDATE_NODE_SERVICE;

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
	protected Class<UPDATE_NODE_SERVICE> getParameterType() {
		return UPDATE_NODE_SERVICE.class;
	}

}
