package fr.openwide.alfresco.repository.core.node.web.script;

import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.api.core.node.service.NodeRemoteService.DELETE_NODE_SERVICE;

public class DeleteNodeWebScript extends AbstractNodeWebScript<Void, DELETE_NODE_SERVICE> {

	@Override
	protected Void execute(DELETE_NODE_SERVICE request) {
		nodeService.delete(
				Objects.requireNonNull(request.nodeReferences, "NodeReferences"));
		return null;
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(DELETE_NODE_SERVICE.class);
	}

}
