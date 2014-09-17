package fr.openwide.alfresco.app.core.node.service;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.springframework.web.client.ResponseExtractor;

import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentSerializer;
import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.EntityEnclosingRestEndpoint;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.GetMethodEndpoint;

public interface NodeService extends NodeRemoteService {

	GetMethodEndpoint<Void> GET_NODE_CONTENT_ENDPOINT = new GetMethodEndpoint<Void>("/api/node/content{property}/{workspace}/{store}/{uuid}") {};
	
	void getNodeContent(NodeReference nodeReference, NameReference property, ResponseExtractor<Void> responseExtractor);

	RepositoryContentData getNodeContent(NodeReference nodeReference, NameReference property, OutputStream out);

	NodeReference create(RepositoryNode node) throws DuplicateChildNodeNameRemoteException;
	NodeReference create(RepositoryNode node, Map<NameReference, RepositoryContentSerializer<?>> serializers) throws DuplicateChildNodeNameRemoteException;
	List<NodeReference> create(List<RepositoryNode> nodes, Map<NameReference, RepositoryContentSerializer<?>> serializers) throws DuplicateChildNodeNameRemoteException;
	
	void update(RepositoryNode node, NodeScope nodeScope) throws DuplicateChildNodeNameRemoteException;
	void update(RepositoryNode node, NodeScope nodeScope, Map<NameReference, RepositoryContentSerializer<?>> serializers) throws DuplicateChildNodeNameRemoteException;
	void update(List<RepositoryNode> nodes, NodeScope nodeScope, Map<NameReference, RepositoryContentSerializer<?>> serializers) throws DuplicateChildNodeNameRemoteException;

	void delete(NodeReference nodeReference);
	
	public List<RepositoryNode> callNodeListSerializer(EntityEnclosingRestEndpoint<List<RepositoryNode>> endPoint,
			Object payload, NodeScope nodeScope);
}
