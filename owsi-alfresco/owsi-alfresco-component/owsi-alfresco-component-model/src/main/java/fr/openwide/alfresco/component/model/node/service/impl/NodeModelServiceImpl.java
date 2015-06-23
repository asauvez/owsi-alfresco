package fr.openwide.alfresco.component.model.node.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.node.binding.NodeContentSerializationParameters;
import fr.openwide.alfresco.api.core.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.component.model.node.model.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.BusinessNodeList;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class NodeModelServiceImpl implements NodeModelService {

	private final NodeService nodeService;

	public NodeModelServiceImpl(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	public BusinessNode get(NodeReference nodeReference, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeRemoteException {
		return new BusinessNode(nodeService.get(nodeReference, nodeScopeBuilder.getScope()));
	}

	@Override
	public Optional<BusinessNode> getOptional(NodeReference nodeReference, NodeScopeBuilder nodeScopeBuilder) {
		try {
			return Optional.of(get(nodeReference, nodeScopeBuilder));
		} catch (NoSuchNodeRemoteException ex) {
			return Optional.absent();
		}
	}
	
	@Override
	public List<BusinessNode> getChildren(NodeReference nodeReference, NodeScopeBuilder nodeScopeBuilder) {
		return getChildren(nodeReference, CmModel.folder.contains, nodeScopeBuilder);
	}
	
	@Override
	public List<BusinessNode> getChildren(NodeReference nodeReference, ChildAssociationModel childAssoc, NodeScopeBuilder nodeScopeBuilder) {
		return new BusinessNodeList(nodeService.getChildren(nodeReference, childAssoc.getNameReference(), nodeScopeBuilder.getScope()));
	}

	@Override
	public List<BusinessNode> getTargetAssocs(NodeReference nodeReference, AssociationModel assoc, NodeScopeBuilder nodeScopeBuilder) {
		return new BusinessNodeList(nodeService.getTargetAssocs(nodeReference, assoc.getNameReference(), nodeScopeBuilder.getScope()));
	}

	@Override
	public List<BusinessNode> getSourceAssocs(NodeReference nodeReference, AssociationModel assoc, NodeScopeBuilder nodeScopeBuilder) {
		return new BusinessNodeList(nodeService.getSourceAssocs(nodeReference, assoc.getNameReference(), nodeScopeBuilder.getScope()));
	}
	
	@Override
	public NodeReference createFolder(NodeReference parentRef, String folderName) throws DuplicateChildNodeNameRemoteException {
		return create(new BusinessNode(parentRef, CmModel.folder, folderName));
	}

	@Override
	public NodeReference createContent(NodeReference parentRef, String fileName, String mimeType, String encoding, Object content) throws DuplicateChildNodeNameRemoteException {
		return create(new BusinessNode(parentRef, CmModel.content, fileName)
				.properties().set(CmModel.content.content, new RepositoryContentData(mimeType, encoding))
				.contents().set(content));
	}

	@Override
	public NodeReference createContent(NodeReference parentRef, final MultipartFile file) throws DuplicateChildNodeNameRemoteException {
		String fileName = FilenameUtils.getName(file.getOriginalFilename());
		return create(new BusinessNode(parentRef, CmModel.content, fileName)
				.properties().set(CmModel.content.content, new RepositoryContentData(file.getContentType(), null))
				.contents().set(file));
	}

	@Override
	public NodeReference create(BusinessNode node) throws DuplicateChildNodeNameRemoteException {
		return nodeService.create(node.getRepositoryNode());
	}
	@Override
	public NodeReference create(BusinessNode node, NodeContentSerializationParameters parameters) throws DuplicateChildNodeNameRemoteException {
		return create(Collections.singletonList(node), parameters).get(0);
	}
	@Override
	public List<NodeReference> create(List<BusinessNode> nodes) throws DuplicateChildNodeNameRemoteException {
		List<RepositoryNode> repoNodes = new ArrayList<>();
		for (BusinessNode node : nodes) {
			repoNodes.add(node.getRepositoryNode());
		}
		return nodeService.create(repoNodes);
	}
	@Override
	public List<NodeReference> create(List<BusinessNode> nodes, NodeContentSerializationParameters parameters) throws DuplicateChildNodeNameRemoteException {
		List<RepositoryNode> repoNodes = new ArrayList<>();
		for (BusinessNode node : nodes) {
			repoNodes.add(node.getRepositoryNode());
		}
		return nodeService.create(repoNodes, parameters);
	}

	@Override
	public void update(BusinessNode node) throws DuplicateChildNodeNameRemoteException {
		update(node, new NodeScopeBuilder()
				.fromNode(node));
	}

	@Override
	public void update(BusinessNode node, NodeScopeBuilder scope) throws DuplicateChildNodeNameRemoteException {
		nodeService.update(node.getRepositoryNode(), scope.getScope());
	}
	@Override
	public void update(List<BusinessNode> nodes, NodeScopeBuilder nodeScopeBuilder) throws DuplicateChildNodeNameRemoteException {
		List<RepositoryNode> repoNodes = new ArrayList<>();
		for (BusinessNode node : nodes) {
			repoNodes.add(node.getRepositoryNode());
		}
		nodeService.update(repoNodes, nodeScopeBuilder.getScope());
	}

	@Override
	public void delete(NodeReference nodeReference) {
		nodeService.delete(nodeReference);
	}
	@Override
	public void delete(List<NodeReference> nodeReferences) {
		nodeService.delete(nodeReferences);
	}
}
