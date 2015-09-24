package fr.openwide.alfresco.repository.core.node.web.script;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService.TARGET_ASSOC_NODE_SERVICE;

public class TargetAssocNodeWebScript extends AbstractNodeListWebScript<TARGET_ASSOC_NODE_SERVICE> {

	@Override
	protected List<RepositoryNode> execute(TARGET_ASSOC_NODE_SERVICE parameter) {
		return nodeService.getTargetAssocs(
				Objects.requireNonNull(parameter.nodeReference, "NodeReference"), 
				Objects.requireNonNull(parameter.assocName, "AssocName"), 
				Objects.requireNonNull(parameter.nodeScope, "NodeScope"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(TARGET_ASSOC_NODE_SERVICE.class);
	}

}
