package fr.openwide.alfresco.app.core.node.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseExtractor;

import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.api.core.node.binding.NodePayloadCallback;
import fr.openwide.alfresco.api.core.node.binding.NodeContentDeserializationParameters;
import fr.openwide.alfresco.api.core.node.binding.NodeContentSerializationParameters;
import fr.openwide.alfresco.api.core.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.api.core.node.model.ContentPropertyWrapper;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RemoteCallParameters;
import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.core.remote.model.endpoint.EntityEnclosingRestEndpoint;

@Service
public class NodeServiceImpl implements NodeService {

	@Autowired
	private RepositoryRemoteBinding repositoryRemoteBinding;
	
	private NodeContentSerializationParameters defaultSerializationParameters = new NodeContentSerializationParameters();
	private NodeContentDeserializationParameters defaultDeserializationParameters = new NodeContentDeserializationParameters();
	
	@Override
	public RepositoryNode get(NodeReference nodeReference, final NodeScope nodeScope, RemoteCallParameters remoteCallParameters) {
		GET_NODE_SERVICE payload = new GET_NODE_SERVICE();
		payload.nodeReference = nodeReference;
		payload.nodeScope = nodeScope;
		payload.remoteCallParameters = remoteCallParameters;
		
		NodeContentDeserializationParameters deserializationParameters = defaultDeserializationParameters;
		if (! nodeScope.getContentDeserializers().isEmpty()) {
			deserializationParameters = deserializationParameters.clone();
			deserializationParameters.getDeserializersByProperties().putAll(nodeScope.getContentDeserializers());
		}
		
		return repositoryRemoteBinding.builderWithSerializer(GET_NODE_SERVICE.ENDPOINT)
			.callPayloadSerializer(payload, null, 
				new NodePayloadCallback<RepositoryNode>() {
					@Override
					public Collection<RepositoryNode> extractNodes(RepositoryNode value) {
						return Collections.singleton(value);
					}
					@Override
					public void doWithPayload(RepositoryNode payload, Map<Integer, ContentPropertyWrapper> wrappers) {
						// on récupére la valeur en retour de la fonction
					}
				}, 
				defaultSerializationParameters, 
				deserializationParameters);
	}

	@Override
	public void getNodeContent(NodeReference nodeReference, NameReference property, ResponseExtractor<Void> responseExtractor) {
		repositoryRemoteBinding.builder(GET_NODE_CONTENT_ENDPOINT)
			.urlVariable((property != null) ? ";" + property.getFullName() : "")
			.urlVariable(nodeReference)
			.call(null, responseExtractor);
	}

	@Override
	public RepositoryContentData getNodeContent(NodeReference nodeReference, NameReference property, final OutputStream out) {
		final RepositoryContentData contentData = new RepositoryContentData();
		getNodeContent(nodeReference, property, new ResponseExtractor<Void>() {
			@Override
			public Void extractData(ClientHttpResponse response) throws IOException {
				HttpHeaders headers = response.getHeaders();
				contentData.setMimetype(headers.getContentType().toString());
				contentData.setSize(headers.getContentLength());
				IOUtils.copy(response.getBody(), out);
				return null;
			}
		});
		return contentData;
	}
	
	@Override
	public List<RepositoryNode> callNodeListSerializer(
			EntityEnclosingRestEndpoint<List<RepositoryNode>> endPoint,
			Object payload,
			NodeScope nodeScope) {
		
		NodeContentDeserializationParameters deserializationParameters = defaultDeserializationParameters;
		if (! nodeScope.getContentDeserializers().isEmpty()) {
			deserializationParameters = deserializationParameters.clone();
			deserializationParameters.getDeserializersByProperties().putAll(nodeScope.getContentDeserializers());
		}
		
		return repositoryRemoteBinding.builderWithSerializer(endPoint)
			.callPayloadSerializer(
				payload, null, 
				new NodePayloadCallback<List<RepositoryNode>>() {
					@Override
					public Collection<RepositoryNode> extractNodes(List<RepositoryNode> nodes) {
						return nodes;
					}
					@Override
					public void doWithPayload(List<RepositoryNode> payload, Map<Integer, ContentPropertyWrapper> wrappers) {
						// on récupére la valeur en retour de la fonction
					}
				}, 
				defaultSerializationParameters, 
				deserializationParameters);
	}
	
	@Override
	public List<RepositoryNode> getChildren(NodeReference nodeReference, NameReference childAssocTypeName, NodeScope nodeScope, RemoteCallParameters remoteCallParameters) {
		CHILDREN_NODE_SERVICE payload = new CHILDREN_NODE_SERVICE();
		payload.nodeReference = nodeReference;
		payload.childAssocTypeName = childAssocTypeName;
		payload.nodeScope = nodeScope;
		payload.remoteCallParameters = remoteCallParameters;
		return callNodeListSerializer(CHILDREN_NODE_SERVICE.ENDPOINT, payload, nodeScope);
	}

	@Override
	public List<RepositoryNode> getTargetAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope, RemoteCallParameters remoteCallParameters) {
		TARGET_ASSOC_NODE_SERVICE payload = new TARGET_ASSOC_NODE_SERVICE();
		payload.nodeReference = nodeReference;
		payload.assocName = assocName;
		payload.nodeScope = nodeScope;
		payload.remoteCallParameters = remoteCallParameters;
		return callNodeListSerializer(TARGET_ASSOC_NODE_SERVICE.ENDPOINT, payload, nodeScope);
	}

	@Override
	public List<RepositoryNode> getSourceAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope, RemoteCallParameters remoteCallParameters) {
		SOURCE_ASSOC_NODE_SERVICE payload = new SOURCE_ASSOC_NODE_SERVICE();
		payload.nodeReference = nodeReference;
		payload.assocName = assocName;
		payload.nodeScope = nodeScope;
		payload.remoteCallParameters = remoteCallParameters;
		return callNodeListSerializer(SOURCE_ASSOC_NODE_SERVICE.ENDPOINT, payload, nodeScope);
	}

	@Override
	public NodeReference create(RepositoryNode node) throws DuplicateChildNodeNameRemoteException {
		return create(node, defaultSerializationParameters);
	}

	@Override
	public List<NodeReference> create(List<RepositoryNode> nodes) throws DuplicateChildNodeNameRemoteException {
		return create(nodes, defaultSerializationParameters);
	}
	
	@Override
	public NodeReference create(RepositoryNode node, NodeContentSerializationParameters parameters) throws DuplicateChildNodeNameRemoteException {
		List<NodeReference> list = create(Collections.singletonList(node), parameters);
		return list.get(0);
	}
	
	private <R> R callNodeUploadSerializer(
			EntityEnclosingRestEndpoint<R> endPoint,
			Object payload,
			List<RepositoryNode> nodes,
			NodeContentSerializationParameters serializationParameters,
			NodeContentDeserializationParameters deserializationParameters) {
		return repositoryRemoteBinding.builderWithSerializer(endPoint)
			.callPayloadSerializer(payload, nodes, null, serializationParameters, deserializationParameters);
	}
	
	@Override
	public List<NodeReference> create(List<RepositoryNode> nodes, NodeContentSerializationParameters serializationParameters) throws DuplicateChildNodeNameRemoteException {
		CREATE_NODE_SERVICE payload = new CREATE_NODE_SERVICE();
		payload.nodes = nodes;
		return callNodeUploadSerializer(CREATE_NODE_SERVICE.ENDPOINT, payload, nodes, serializationParameters, defaultDeserializationParameters);
	}
	
	@Override
	public void update(List<RepositoryNode> nodes, NodeScope nodeScope) throws DuplicateChildNodeNameRemoteException {
		update(nodes, nodeScope, defaultSerializationParameters);
	}
	@Override
	public void update(RepositoryNode node, NodeScope nodeScope) throws DuplicateChildNodeNameRemoteException {
		update(node, nodeScope, defaultSerializationParameters);
	}
	@Override
	public void update(RepositoryNode node, NodeScope nodeScope,
			NodeContentSerializationParameters serializationParameters) throws DuplicateChildNodeNameRemoteException {
		update(Collections.singletonList(node), nodeScope, serializationParameters);
	}

	@Override
	public void update(List<RepositoryNode> nodes, NodeScope nodeScope, 
			NodeContentSerializationParameters serializationParameters) throws DuplicateChildNodeNameRemoteException {
		UPDATE_NODE_SERVICE payload = new UPDATE_NODE_SERVICE();
		payload.nodes = nodes;
		payload.nodeScope = nodeScope;
		callNodeUploadSerializer(UPDATE_NODE_SERVICE.ENDPOINT, payload, nodes, serializationParameters, defaultDeserializationParameters);
	}
	
	@Override
	public void delete(NodeReference nodeReference) {
		delete(Collections.singletonList(nodeReference));
	}
	
	@Override
	public void delete(List<NodeReference> nodeReferences) {
		DELETE_NODE_SERVICE payload = new DELETE_NODE_SERVICE();
		payload.nodeReferences = nodeReferences;
		callNodeUploadSerializer(DELETE_NODE_SERVICE.ENDPOINT, payload, null, defaultSerializationParameters, defaultDeserializationParameters);
	}

}
