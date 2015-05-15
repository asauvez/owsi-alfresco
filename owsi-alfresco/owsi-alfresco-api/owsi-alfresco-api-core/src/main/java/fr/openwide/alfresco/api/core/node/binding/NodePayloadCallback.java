package fr.openwide.alfresco.api.core.node.binding;

import java.util.Collection;
import java.util.Map;

import fr.openwide.alfresco.api.core.node.model.ContentPropertyWrapper;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;

public interface NodePayloadCallback<P> {

	Collection<RepositoryNode> extractNodes(P payload);

	void doWithPayload(P payload, Map<Integer, ContentPropertyWrapper> wrappers);

}
