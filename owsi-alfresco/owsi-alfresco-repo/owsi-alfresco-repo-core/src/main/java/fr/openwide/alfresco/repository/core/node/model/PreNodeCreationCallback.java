package fr.openwide.alfresco.repository.core.node.model;

import fr.openwide.alfresco.api.core.node.model.RepositoryNode;


public interface PreNodeCreationCallback {

	void onPreNodeCreationCallback(RepositoryNode node);

}
