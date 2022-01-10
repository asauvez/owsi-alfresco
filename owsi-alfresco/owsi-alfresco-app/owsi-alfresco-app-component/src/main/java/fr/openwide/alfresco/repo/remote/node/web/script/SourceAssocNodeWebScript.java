package fr.openwide.alfresco.repo.remote.node.web.script;

import java.util.List;
import java.util.Objects;

import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService.SOURCE_ASSOC_NODE_SERVICE;

public class SourceAssocNodeWebScript extends AbstractNodeListWebScript<SOURCE_ASSOC_NODE_SERVICE> {

	@Override
	protected List<RepositoryNode> execute(SOURCE_ASSOC_NODE_SERVICE parameter) {
		return nodeService.getTargetAssocs(
				Objects.requireNonNull(parameter.nodeReference, "NodeReference"), 
				Objects.requireNonNull(parameter.assocName, "AssocName"), 
				Objects.requireNonNull(parameter.nodeScope, "NodeScope"));
	}

	@Override
	protected Class<SOURCE_ASSOC_NODE_SERVICE> getParameterType() {
		return SOURCE_ASSOC_NODE_SERVICE.class;
	}
}
