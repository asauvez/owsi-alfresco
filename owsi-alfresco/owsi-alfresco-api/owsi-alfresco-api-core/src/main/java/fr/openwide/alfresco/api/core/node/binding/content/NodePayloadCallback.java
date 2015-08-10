package fr.openwide.alfresco.api.core.node.binding.content;

import java.util.Collection;

import fr.openwide.alfresco.api.core.node.binding.RemoteCallPayload;
import fr.openwide.alfresco.api.core.node.model.ContentPropertyWrapper;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;

public interface NodePayloadCallback<P> {

	Collection<RepositoryNode> extractNodes(P payload);

	void doWithPayload(RemoteCallPayload<P> remoteCallPayload, Collection<ContentPropertyWrapper> wrappers);

}
