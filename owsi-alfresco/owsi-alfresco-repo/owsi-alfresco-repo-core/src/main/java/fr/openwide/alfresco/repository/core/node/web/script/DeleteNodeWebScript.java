package fr.openwide.alfresco.repository.core.node.web.script;

import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class DeleteNodeWebScript extends AbstractNodeWebScript<Void, NodeReference> {

	@Override
	protected Void execute(NodeReference nodeReference) {
		nodeService.delete(
				Objects.requireNonNull(nodeReference, "NodeReference"));
		return null;
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(NodeReference.class);
	}

}
