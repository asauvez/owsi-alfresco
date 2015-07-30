package fr.openwide.alfresco.app.core.node.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import fr.openwide.alfresco.api.core.node.binding.RemoteCallPayload;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentDeserializationParameters;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentDeserializer;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializationParameters;
import fr.openwide.alfresco.api.core.node.binding.content.NodePayloadCallback;
import fr.openwide.alfresco.api.core.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.api.core.node.model.ContentPropertyWrapper;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.model.RepositoryVisitor;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.core.remote.model.endpoint.EntityEnclosingRemoteEndpoint;
import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;

public class NodeServiceImpl implements NodeService {

	private final RepositoryRemoteBinding repositoryRemoteBinding;

	private NodeContentSerializationParameters defaultSerializationParameters = new NodeContentSerializationParameters();
	private NodeContentDeserializationParameters defaultDeserializationParameters = new NodeContentDeserializationParameters();

	public NodeServiceImpl(RepositoryRemoteBinding repositoryRemoteBinding) {
		this.repositoryRemoteBinding = repositoryRemoteBinding;
	}

	private void initDeserializer(NodeScope nodeScope, final NodeContentDeserializationParameters deserializationParameters) {
		nodeScope.visit(new RepositoryVisitor<NodeScope>() {
			@Override
			public void visit(NodeScope nodeScope) {
				if (! nodeScope.getContentDeserializers().isEmpty()) {
					for (Entry<NameReference, NodeContentDeserializer<?>> entry : nodeScope.getContentDeserializers().entrySet()) {
						push(entry.getKey());
						deserializationParameters.getDeserializersByPath().put(getCurrentPath(), entry.getValue());
						pop(entry.getKey());
					}
				}
			}
			
		});
	}
	
	@Override
	public RepositoryNode get(NodeReference nodeReference, final NodeScope nodeScope) {
		GET_NODE_SERVICE payload = new GET_NODE_SERVICE();
		payload.nodeReference = nodeReference;
		payload.nodeScope = nodeScope;
		
		return callNodeSerializer(GET_NODE_SERVICE.ENDPOINT, payload, nodeScope);
	}

	@Override
	public RepositoryNode callNodeSerializer(EntityEnclosingRemoteEndpoint<RepositoryNode> endPoint, Object payload, NodeScope nodeScope) {
		NodeContentDeserializationParameters deserializationParameters = defaultDeserializationParameters.clone();
		initDeserializer(nodeScope, deserializationParameters);
		
		return repositoryRemoteBinding.builderWithSerializer(endPoint)
			.callPayloadSerializer(payload, null, 
				new NodePayloadCallback<RepositoryNode>() {
					@Override
					public Collection<RepositoryNode> extractNodes(RepositoryNode value) {
						return Collections.singleton(value);
					}
					@Override
					public void doWithPayload(RemoteCallPayload<RepositoryNode> payload, Collection<ContentPropertyWrapper> wrappers) {
						// on récupére la valeur en retour de la fonction
					}
				}, 
				defaultSerializationParameters, 
				deserializationParameters);
	}
	
	@Override
	public List<RepositoryNode> callNodeListSerializer(
			EntityEnclosingRemoteEndpoint<List<RepositoryNode>> endPoint,
			Object payload,
			NodeScope nodeScope) {
		
		NodeContentDeserializationParameters deserializationParameters = defaultDeserializationParameters.clone();
		initDeserializer(nodeScope, deserializationParameters);
		
		return repositoryRemoteBinding.builderWithSerializer(endPoint)
			.callPayloadSerializer(
				payload, null, 
				new NodePayloadCallback<List<RepositoryNode>>() {
					@Override
					public Collection<RepositoryNode> extractNodes(List<RepositoryNode> nodes) {
						return nodes;
					}
					@Override
					public void doWithPayload(RemoteCallPayload<List<RepositoryNode>> payload, Collection<ContentPropertyWrapper> wrappers) {
						// on récupére la valeur en retour de la fonction
					}
				}, 
				defaultSerializationParameters, 
				deserializationParameters);
	}

	@Override
	public List<RepositoryNode> getChildren(NodeReference nodeReference, NameReference childAssocTypeName, NodeScope nodeScope) {
		CHILDREN_NODE_SERVICE payload = new CHILDREN_NODE_SERVICE();
		payload.nodeReference = nodeReference;
		payload.childAssocTypeName = childAssocTypeName;
		payload.nodeScope = nodeScope;
		return callNodeListSerializer(CHILDREN_NODE_SERVICE.ENDPOINT, payload, nodeScope);
	}

	@Override
	public List<RepositoryNode> getTargetAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope) {
		TARGET_ASSOC_NODE_SERVICE payload = new TARGET_ASSOC_NODE_SERVICE();
		payload.nodeReference = nodeReference;
		payload.assocName = assocName;
		payload.nodeScope = nodeScope;
		return callNodeListSerializer(TARGET_ASSOC_NODE_SERVICE.ENDPOINT, payload, nodeScope);
	}

	@Override
	public List<RepositoryNode> getSourceAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope) {
		SOURCE_ASSOC_NODE_SERVICE payload = new SOURCE_ASSOC_NODE_SERVICE();
		payload.nodeReference = nodeReference;
		payload.assocName = assocName;
		payload.nodeScope = nodeScope;
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
			EntityEnclosingRemoteEndpoint<R> endPoint,
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
