package fr.openwide.alfresco.repo.remote.node.web.script;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService.CREATE_NODE_SERVICE;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;

public class CreateNodeWebScript extends AbstractNodeWebScript<List<NodeReference>, CREATE_NODE_SERVICE> {

	@Override
	protected List<NodeReference> execute(CREATE_NODE_SERVICE request) {
		return nodeService.create(Objects.requireNonNull(request.nodes, "Nodes"));
	}

	@Override
	protected Collection<RepositoryNode> getInputNodes(CREATE_NODE_SERVICE payload) {
		return payload.nodes;
	}
	
	@Override
	protected Class<CREATE_NODE_SERVICE> getParameterType() {
		return CREATE_NODE_SERVICE.class;
	}

}
