package fr.openwide.alfresco.repository.api.node.binding;

import java.util.Collection;
import java.util.Map;

import fr.openwide.alfresco.repository.api.node.model.ContentPropertyWrapper;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;

public interface NodePayloadCallback<P> {

	Collection<RepositoryNode> extractNodes(P payload);

	void doWithPayload(P payload, Map<Integer, ContentPropertyWrapper> wrappers);

}
