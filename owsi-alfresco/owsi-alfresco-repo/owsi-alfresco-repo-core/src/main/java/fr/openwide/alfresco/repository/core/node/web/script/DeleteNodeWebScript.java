package fr.openwide.alfresco.repository.core.node.web.script;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class DeleteNodeWebScript extends AbstractNodeWebScript<Void, List<NodeReference>> {

	@Override
	protected Void execute(List<NodeReference> nodeReferences) {
		nodeService.delete(
				Objects.requireNonNull(nodeReferences, "NodeReferences"));
		return null;
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(NodeReference.class);
	}

}
