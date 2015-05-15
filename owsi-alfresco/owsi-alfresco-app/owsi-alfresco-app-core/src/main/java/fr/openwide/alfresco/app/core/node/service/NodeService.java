package fr.openwide.alfresco.app.core.node.service;

import java.io.OutputStream;
import java.util.List;

import org.springframework.web.client.ResponseExtractor;

import fr.openwide.alfresco.api.core.node.binding.NodeContentSerializationParameters;
import fr.openwide.alfresco.api.core.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.core.remote.model.endpoint.EntityEnclosingRemoteEndpoint;
import fr.openwide.alfresco.api.core.remote.model.endpoint.GetMethodEndpoint;

public interface NodeService extends NodeRemoteService {

	GetMethodEndpoint<Void> GET_NODE_CONTENT_ENDPOINT = new GetMethodEndpoint<Void>("/api/node/content{property}/{workspace}/{store}/{uuid}") {};
	
	void getNodeContent(NodeReference nodeReference, NameReference property, ResponseExtractor<Void> responseExtractor);

	RepositoryContentData getNodeContent(NodeReference nodeReference, NameReference property, OutputStream out);

	NodeReference create(RepositoryNode node) throws DuplicateChildNodeNameRemoteException;
	NodeReference create(RepositoryNode node, NodeContentSerializationParameters parameters) throws DuplicateChildNodeNameRemoteException;
	List<NodeReference> create(List<RepositoryNode> nodes, NodeContentSerializationParameters parameters) throws DuplicateChildNodeNameRemoteException;
	
	void update(RepositoryNode node, NodeScope nodeScope) throws DuplicateChildNodeNameRemoteException;
	void update(RepositoryNode node, NodeScope nodeScope, NodeContentSerializationParameters parameters) throws DuplicateChildNodeNameRemoteException;
	void update(List<RepositoryNode> nodes, NodeScope nodeScope, NodeContentSerializationParameters parameters) throws DuplicateChildNodeNameRemoteException;

	void delete(NodeReference nodeReference);
	
	public List<RepositoryNode> callNodeListSerializer(EntityEnclosingRemoteEndpoint<List<RepositoryNode>> endPoint,
			Object payload, NodeScope nodeScope);
}
