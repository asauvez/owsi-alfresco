package fr.openwide.alfresco.repo.core.node.web.script;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService.CHILDREN_NODE_SERVICE;

public class ChildrenNodeWebScript extends AbstractNodeListWebScript<CHILDREN_NODE_SERVICE> {

	@Override
	protected List<RepositoryNode> execute(CHILDREN_NODE_SERVICE parameter) {
		return nodeService.getChildren(
				Objects.requireNonNull(parameter.nodeReference, "NodeReference"), 
				Objects.requireNonNull(parameter.childAssocTypeName, "ChildAssocTypeName"), 
				Objects.requireNonNull(parameter.nodeScope, "NodeScope"));
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(CHILDREN_NODE_SERVICE.class);
	}

}
