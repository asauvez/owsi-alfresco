package fr.openwide.alfresco.app.core.node.service.impl;

import java.util.Collections;
import java.util.List;

import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializationParameters;
import fr.openwide.alfresco.api.core.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;

public class NodeServiceImpl implements NodeService {

	private final RepositoryRemoteBinding repositoryRemoteBinding;

	public NodeServiceImpl(RepositoryRemoteBinding repositoryRemoteBinding) {
		this.repositoryRemoteBinding = repositoryRemoteBinding;
	}
	
	@Override
	public RepositoryNode get(NodeReference nodeReference, final NodeScope nodeScope) {
		GET_NODE_SERVICE payload = new GET_NODE_SERVICE();
		payload.nodeReference = nodeReference;
		payload.nodeScope = nodeScope;
		
		return repositoryRemoteBinding.callNodeSerializer(payload, nodeScope);
	}
	
	@Override
	public List<RepositoryNode> getChildren(NodeReference nodeReference, NameReference childAssocTypeName, NodeScope nodeScope) {
		CHILDREN_NODE_SERVICE payload = new CHILDREN_NODE_SERVICE();
		payload.nodeReference = nodeReference;
		payload.childAssocTypeName = childAssocTypeName;
		payload.nodeScope = nodeScope;
		return repositoryRemoteBinding.callNodeListSerializer(payload, nodeScope);
	}

	@Override
	public List<RepositoryNode> getTargetAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope) {
		TARGET_ASSOC_NODE_SERVICE payload = new TARGET_ASSOC_NODE_SERVICE();
		payload.nodeReference = nodeReference;
		payload.assocName = assocName;
		payload.nodeScope = nodeScope;
		return repositoryRemoteBinding.callNodeListSerializer(payload, nodeScope);
	}

	@Override
	public List<RepositoryNode> getSourceAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope) {
		SOURCE_ASSOC_NODE_SERVICE payload = new SOURCE_ASSOC_NODE_SERVICE();
		payload.nodeReference = nodeReference;
		payload.assocName = assocName;
		payload.nodeScope = nodeScope;
		return repositoryRemoteBinding.callNodeListSerializer(payload, nodeScope);
	}

	@Override
	public NodeReference create(RepositoryNode node) throws DuplicateChildNodeNameRemoteException {
		return create(node, repositoryRemoteBinding.getDefaultSerializationParameters());
	}

	@Override
	public List<NodeReference> create(List<RepositoryNode> nodes) throws DuplicateChildNodeNameRemoteException {
		return create(nodes, repositoryRemoteBinding.getDefaultSerializationParameters());
	}

	@Override
	public NodeReference create(RepositoryNode node, NodeContentSerializationParameters parameters) throws DuplicateChildNodeNameRemoteException {
		List<NodeReference> list = create(Collections.singletonList(node), parameters);
		return list.get(0);
	}
	
	@Override
	public List<NodeReference> create(List<RepositoryNode> nodes, NodeContentSerializationParameters serializationParameters) throws DuplicateChildNodeNameRemoteException {
		CREATE_NODE_SERVICE payload = new CREATE_NODE_SERVICE();
		payload.nodes = nodes;
		return repositoryRemoteBinding.callNodeUploadSerializer(payload, nodes, serializationParameters, repositoryRemoteBinding.getDefaultDeserializationParameters());
	}

	@Override
	public void update(List<RepositoryNode> nodes, NodeScope nodeScope) throws DuplicateChildNodeNameRemoteException {
		update(nodes, nodeScope, repositoryRemoteBinding.getDefaultSerializationParameters());
	}

	@Override
	public void update(RepositoryNode node, NodeScope nodeScope) throws DuplicateChildNodeNameRemoteException {
		update(node, nodeScope, repositoryRemoteBinding.getDefaultSerializationParameters());
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
		repositoryRemoteBinding.callNodeUploadSerializer(payload, nodes, serializationParameters, repositoryRemoteBinding.getDefaultDeserializationParameters());
	}

	@Override
	public void delete(NodeReference nodeReference) {
		delete(Collections.singletonList(nodeReference));
	}

	@Override
	public void delete(List<NodeReference> nodeReferences) {
		DELETE_NODE_SERVICE payload = new DELETE_NODE_SERVICE();
		payload.nodeReferences = nodeReferences;
		repositoryRemoteBinding.callNodeUploadSerializer(payload, null, repositoryRemoteBinding.getDefaultSerializationParameters(), repositoryRemoteBinding.getDefaultDeserializationParameters());
	}

}
