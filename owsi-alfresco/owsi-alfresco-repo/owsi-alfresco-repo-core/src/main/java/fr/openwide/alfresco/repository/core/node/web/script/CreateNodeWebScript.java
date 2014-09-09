package fr.openwide.alfresco.repository.core.node.web.script;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService.CREATE_NODE_SERVICE;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class CreateNodeWebScript extends AbstractNodeWebScript<List<NodeReference>, CREATE_NODE_SERVICE> {

	@Override
	protected List<NodeReference> execute(CREATE_NODE_SERVICE request) {
		return nodeService.create(Objects.requireNonNull(request.nodes, "Nodes"));
	}

	@Override
	protected Collection<RepositoryNode> getUploadedNodes(CREATE_NODE_SERVICE payload) {
		return payload.nodes;
	}
	
	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(CREATE_NODE_SERVICE.class);
	}

}
