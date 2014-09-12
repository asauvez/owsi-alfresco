package fr.openwide.alfresco.app.core.node.service;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.springframework.web.client.ResponseExtractor;

import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.serializer.RepositoryContentSerializer;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.GetMethodEndpoint;

public interface NodeService extends NodeRemoteService {

	GetMethodEndpoint<Void> GET_NODE_CONTENT_ENDPOINT = new GetMethodEndpoint<Void>("/api/node/content{property}/{workspace}/{store}/{uuid}") {};
	
	void getNodeContent(NodeReference nodeReference, NameReference property, ResponseExtractor<Void> responseExtractor);

	RepositoryContentData getNodeContent(NodeReference nodeReference, NameReference property, OutputStream out);

	NodeReference create(RepositoryNode node) throws DuplicateChildNameException;
	NodeReference create(RepositoryNode node, Map<NameReference, RepositoryContentSerializer<?>> serializers) throws DuplicateChildNameException;
	List<NodeReference> create(List<RepositoryNode> nodes, Map<NameReference, RepositoryContentSerializer<?>> serializers) throws DuplicateChildNameException;
	
	void update(RepositoryNode node, NodeScope nodeScope) throws DuplicateChildNameException;
	void update(RepositoryNode node, NodeScope nodeScope, Map<NameReference, RepositoryContentSerializer<?>> serializers) throws DuplicateChildNameException;
	void update(List<RepositoryNode> nodes, NodeScope nodeScope, Map<NameReference, RepositoryContentSerializer<?>> serializers) throws DuplicateChildNameException;

	void delete(NodeReference nodeReference);
}
