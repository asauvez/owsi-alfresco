package fr.openwide.alfresco.repo.remote.node.web.script;

import java.util.Objects;

import fr.openwide.alfresco.api.core.node.service.NodeRemoteService.DELETE_NODE_SERVICE;

public class DeleteNodeWebScript extends AbstractNodeWebScript<Void, DELETE_NODE_SERVICE> {

	@Override
	protected Void execute(DELETE_NODE_SERVICE request) {
		nodeService.delete(
				Objects.requireNonNull(request.nodeReferences, "NodeReferences"));
		return null;
	}

	@Override
	protected Class<DELETE_NODE_SERVICE> getParameterType() {
		return DELETE_NODE_SERVICE.class;
	}

}
