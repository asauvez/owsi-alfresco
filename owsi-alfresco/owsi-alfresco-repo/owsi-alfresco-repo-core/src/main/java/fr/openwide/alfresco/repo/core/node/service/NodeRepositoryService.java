package fr.openwide.alfresco.repo.core.node.service;

import fr.openwide.alfresco.api.core.node.service.NodeRemoteService;
import fr.openwide.alfresco.repo.core.node.model.PreNodeCreationCallback;

public interface NodeRepositoryService extends NodeRemoteService {

	void addPreNodeCreationCallback(PreNodeCreationCallback callback);

}
