package fr.openwide.alfresco.app.core.node.service;

import java.util.List;

import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializationParameters;
import fr.openwide.alfresco.api.core.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.core.remote.model.endpoint.EntityEnclosingRemoteEndpoint;

public interface NodeService extends NodeRemoteService {

	NodeReference create(RepositoryNode node) throws DuplicateChildNodeNameRemoteException;
	NodeReference create(RepositoryNode node, NodeContentSerializationParameters parameters) throws DuplicateChildNodeNameRemoteException;
	List<NodeReference> create(List<RepositoryNode> nodes, NodeContentSerializationParameters parameters) throws DuplicateChildNodeNameRemoteException;
	
	void update(RepositoryNode node, NodeScope nodeScope) throws DuplicateChildNodeNameRemoteException;
	void update(RepositoryNode node, NodeScope nodeScope, NodeContentSerializationParameters parameters) throws DuplicateChildNodeNameRemoteException;
	void update(List<RepositoryNode> nodes, NodeScope nodeScope, NodeContentSerializationParameters parameters) throws DuplicateChildNodeNameRemoteException;

	void delete(NodeReference nodeReference);
	
	public RepositoryNode callNodeSerializer(EntityEnclosingRemoteEndpoint<RepositoryNode> endPoint,
			Object payload, NodeScope nodeScope);
	public List<RepositoryNode> callNodeListSerializer(EntityEnclosingRemoteEndpoint<List<RepositoryNode>> endPoint,
			Object payload, NodeScope nodeScope);
}
