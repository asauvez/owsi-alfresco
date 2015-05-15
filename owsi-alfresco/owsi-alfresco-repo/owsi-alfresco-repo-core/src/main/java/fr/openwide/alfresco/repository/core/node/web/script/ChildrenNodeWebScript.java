package fr.openwide.alfresco.repository.core.node.web.script;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.node.model.RemoteCallParameters;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService.CHILDREN_NODE_SERVICE;

public class ChildrenNodeWebScript extends AbstractNodeListWebScript<CHILDREN_NODE_SERVICE> {

	@Override
	protected List<RepositoryNode> execute(CHILDREN_NODE_SERVICE parameter) {
		return nodeService.getChildren(
				Objects.requireNonNull(parameter.nodeReference, "NodeReference"), 
				Objects.requireNonNull(parameter.childAssocTypeName, "ChildAssocTypeName"), 
				Objects.requireNonNull(parameter.nodeScope, "NodeScope"),
				Objects.requireNonNull(parameter.remoteCallParameters, "RemoteCallParameters"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(CHILDREN_NODE_SERVICE.class);
	}

	@Override
	protected RemoteCallParameters getRemoteCallParameters(CHILDREN_NODE_SERVICE payload) {
		return payload.remoteCallParameters;
	}
}
