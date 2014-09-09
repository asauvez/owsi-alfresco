package fr.openwide.alfresco.repository.core.node.web.script;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService.SOURCE_ASSOC_NODE_SERVICE;

public class SourceAssocNodeWebScript extends AbstractNodeListWebScript<SOURCE_ASSOC_NODE_SERVICE> {

	@Override
	protected List<RepositoryNode> execute(SOURCE_ASSOC_NODE_SERVICE parameter) {
		return nodeService.getTargetAssocs(
				Objects.requireNonNull(parameter.nodeReference, "NodeReference"), 
				Objects.requireNonNull(parameter.assocName, "AssocName"), 
				Objects.requireNonNull(parameter.nodeScope, "NodeScope"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(SOURCE_ASSOC_NODE_SERVICE.class);
	}

}
