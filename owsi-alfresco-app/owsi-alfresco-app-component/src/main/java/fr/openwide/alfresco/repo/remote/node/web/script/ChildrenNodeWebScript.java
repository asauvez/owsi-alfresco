package fr.openwide.alfresco.repo.remote.node.web.script;

import java.util.List;
import java.util.Objects;

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
	protected Class<CHILDREN_NODE_SERVICE> getParameterType() {
		return CHILDREN_NODE_SERVICE.class;
	}

}
