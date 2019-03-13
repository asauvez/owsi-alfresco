package fr.openwide.alfresco.component.model.node.service.impl;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import fr.openwide.alfresco.api.core.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.BusinessNodeList;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.model.association.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.ManyToManyAssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.ManyToOneAssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.OneToManyAssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.OneToOneAssociationModel;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class NodeModelServiceImpl implements NodeModelService {

	private final NodeRemoteService nodeService;

	public NodeModelServiceImpl(NodeRemoteService nodeService) {
		this.nodeService = nodeService;
	}

	@Override
	public BusinessNode get(NodeReference nodeReference, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeRemoteException {
		return new BusinessNode(nodeService.get(nodeReference, nodeScopeBuilder.getScope()));
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
	public List<BusinessNode> getTargetAssocs(NodeReference nodeReference, ManyToManyAssociationModel assoc, NodeScopeBuilder nodeScopeBuilder) {
		return _getTargetAssocs(nodeReference, assoc, nodeScopeBuilder);
	}
	@Override
	public List<BusinessNode> getSourceAssocs(NodeReference nodeReference, ManyToManyAssociationModel assoc, NodeScopeBuilder nodeScopeBuilder) {
		return _getSourceAssocs(nodeReference, assoc, nodeScopeBuilder);
	}
	@Override
	public Optional<BusinessNode> getTargetAssocs(NodeReference nodeReference, ManyToOneAssociationModel assoc, NodeScopeBuilder nodeScopeBuilder) {
		return toOptional(_getTargetAssocs(nodeReference, assoc, nodeScopeBuilder));
	}
	@Override
	public List<BusinessNode> getSourceAssocs(NodeReference nodeReference, ManyToOneAssociationModel assoc, NodeScopeBuilder nodeScopeBuilder) {
		return _getSourceAssocs(nodeReference, assoc, nodeScopeBuilder);
	}
	@Override
	public List<BusinessNode> getTargetAssocs(NodeReference nodeReference, OneToManyAssociationModel assoc, NodeScopeBuilder nodeScopeBuilder) {
		return _getTargetAssocs(nodeReference, assoc, nodeScopeBuilder);
	}
	@Override
	public Optional<BusinessNode> getSourceAssocs(NodeReference nodeReference, OneToManyAssociationModel assoc, NodeScopeBuilder nodeScopeBuilder) {
		return toOptional(_getSourceAssocs(nodeReference, assoc, nodeScopeBuilder));
	}
	@Override
	public Optional<BusinessNode> getTargetAssocs(NodeReference nodeReference, OneToOneAssociationModel assoc, NodeScopeBuilder nodeScopeBuilder) {
		return toOptional(_getTargetAssocs(nodeReference, assoc, nodeScopeBuilder));
	}
	@Override
	public Optional<BusinessNode> getSourceAssocs(NodeReference nodeReference, OneToOneAssociationModel assoc, NodeScopeBuilder nodeScopeBuilder) {
		return getSourceAssocs(nodeReference, assoc, nodeScopeBuilder);
	}
	private List<BusinessNode> _getTargetAssocs(NodeReference nodeReference, AssociationModel assoc, NodeScopeBuilder nodeScopeBuilder) {
		return new BusinessNodeList(nodeService.getTargetAssocs(nodeReference, assoc.getNameReference(), nodeScopeBuilder.getScope()));
	}
	private List<BusinessNode> _getSourceAssocs(NodeReference nodeReference, AssociationModel assoc, NodeScopeBuilder nodeScopeBuilder) {
		return new BusinessNodeList(nodeService.getSourceAssocs(nodeReference, assoc.getNameReference(), nodeScopeBuilder.getScope()));
	}
	protected <T> Optional<T> toOptional(List<T> list) {
		if (list.isEmpty()) return Optional.empty();
		if (list.size() == 1) return Optional.of(list.get(0));
		throw new IllegalStateException("list=" + list);
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
	public NodeReference createContent(NodeReference parentRef, String fileName, MediaType mimeType, Charset encoding, Object content) throws DuplicateChildNodeNameRemoteException {
		return create(new BusinessNode(parentRef, CmModel.content, fileName)
				.properties().set(CmModel.content.content, new RepositoryContentData(mimeType, encoding))
				.contents().set(content));
	}

	@Override
	public NodeReference createContent(NodeReference parentRef, final MultipartFile file) throws DuplicateChildNodeNameRemoteException {
		int index = file.getOriginalFilename().replace('\\',  '/').lastIndexOf('/');
		String fileName = (index != -1) ? file.getOriginalFilename().substring(index + 1) : file.getOriginalFilename();
		
		return create(new BusinessNode(parentRef, CmModel.content, fileName)
				.properties().set(CmModel.content.content, new RepositoryContentData(file.getContentType(), null))
				.contents().set(file));
	}

	@Override
	public NodeReference create(BusinessNode node) throws DuplicateChildNodeNameRemoteException {
		return nodeService.create(Collections.singletonList(node.getRepositoryNode())).get(0);
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
	public void update(BusinessNode node) throws DuplicateChildNodeNameRemoteException {
		update(node, new NodeScopeBuilder()
				.fromNode(node));
	}

	@Override
	public void update(BusinessNode node, NodeScopeBuilder scope) throws DuplicateChildNodeNameRemoteException {
		nodeService.update(Collections.singletonList(node.getRepositoryNode()), scope.getScope());
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
		nodeService.delete(Collections.singletonList(nodeReference));
	}
	@Override
	public void delete(List<NodeReference> nodeReferences) {
		nodeService.delete(nodeReferences);
	}
}
