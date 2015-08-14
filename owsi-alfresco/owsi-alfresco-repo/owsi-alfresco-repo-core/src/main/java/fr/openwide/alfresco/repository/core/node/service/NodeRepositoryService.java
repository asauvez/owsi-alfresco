package fr.openwide.alfresco.repository.core.node.service;

import fr.openwide.alfresco.api.core.node.service.NodeRemoteService;
import fr.openwide.alfresco.repository.core.node.model.PreNodeCreationCallback;

public interface NodeRepositoryService extends NodeRemoteService {

	void addPreNodeCreationCallback(PreNodeCreationCallback callback);

}
