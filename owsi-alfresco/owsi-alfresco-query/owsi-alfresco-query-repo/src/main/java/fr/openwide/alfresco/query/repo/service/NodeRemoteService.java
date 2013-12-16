package fr.openwide.alfresco.query.repo.service;

import fr.openwide.alfresco.query.api.node.model.NodeReference;
import fr.openwide.alfresco.query.api.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.api.search.model.NodeResult;

public interface NodeRemoteService {

	NodeReference create(NodeResult node);

	void update(NodeResult node, NodeFetchDetails details);

	void delete(NodeReference nodeReference);

}
