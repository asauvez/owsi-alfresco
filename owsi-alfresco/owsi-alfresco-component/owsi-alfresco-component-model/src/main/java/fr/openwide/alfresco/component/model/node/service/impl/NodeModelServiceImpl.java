package fr.openwide.alfresco.component.model.node.service.impl;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import fr.openwide.alfresco.app.core.node.binding.MultipartFileRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.component.model.node.model.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.model.property.single.ContentPropertyModel;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentSerializer;
import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.exception.NoSuchNodeException;
import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class NodeModelServiceImpl implements NodeModelService {

	@Autowired
	private NodeService nodeService;

	@Override
	public BusinessNode get(NodeReference nodeReference, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeException {
		return new BusinessNode(nodeService.get(nodeReference, nodeScopeBuilder.getScope()));
	}

	@Override
	public RepositoryContentData getNodeContent(NodeReference nodeReference, OutputStream out) {
		return getNodeContent(nodeReference, CmModel.content.content, out);
	}

	@Override
	public RepositoryContentData getNodeContent(NodeReference nodeReference, ContentPropertyModel property, OutputStream out) {
		return nodeService.getNodeContent(nodeReference, property.getNameReference(), out);
	}

	@Override
	public List<BusinessNode> getChildren(NodeReference nodeReference, NodeScopeBuilder nodeScopeBuilder) {
		return getChildren(nodeReference, CmModel.folder.contains, nodeScopeBuilder);
	}
	
	@Override
	public List<BusinessNode> getChildren(NodeReference nodeReference, ChildAssociationModel childAssoc, NodeScopeBuilder nodeScopeBuilder) {
		return BusinessNode.wrapList(nodeService.getChildren(nodeReference, childAssoc.getNameReference(), nodeScopeBuilder.getScope()));
	}

	@Override
	public List<BusinessNode> getTargetAssocs(NodeReference nodeReference, AssociationModel assoc, NodeScopeBuilder nodeScopeBuilder) {
		return BusinessNode.wrapList(nodeService.getTargetAssocs(nodeReference, assoc.getNameReference(), nodeScopeBuilder.getScope()));
	}

	@Override
	public List<BusinessNode> getSourceAssocs(NodeReference nodeReference, AssociationModel assoc, NodeScopeBuilder nodeScopeBuilder) {
		return BusinessNode.wrapList(nodeService.getSourceAssocs(nodeReference, assoc.getNameReference(), nodeScopeBuilder.getScope()));
	}
	
	@Override
	public NodeReference createFolder(NodeReference parentRef, String folderName) throws DuplicateChildNameException {
		return create(new BusinessNode(parentRef, CmModel.folder, folderName));
	}

	@Override
	public NodeReference createContent(NodeReference parentRef, String fileName, String mimeType, String encoding, Object content) throws DuplicateChildNameException {
		return create(new BusinessNode(parentRef, CmModel.content, fileName)
				.property(CmModel.content.content, new RepositoryContentData(mimeType, encoding))
				.content(content));
	}

	@Override
	public NodeReference createContent(NodeReference parentRef, final MultipartFile file) throws DuplicateChildNameException {
		String fileName = FilenameUtils.getName(file.getOriginalFilename());
		return create(new BusinessNode(parentRef, CmModel.content, fileName)
				.property(CmModel.content.content, new RepositoryContentData(file.getContentType(), null))
				.content(file),
			MultipartFileRepositoryContentSerializer.INSTANCE);
	}

	@Override
	public NodeReference create(BusinessNode node) throws DuplicateChildNameException {
		return nodeService.create(node.getRepositoryNode());
	}
	@Override
	public NodeReference create(BusinessNode node, RepositoryContentSerializer<?> serializer) throws DuplicateChildNameException {
		Map<ContentPropertyModel, RepositoryContentSerializer<?>> serializers = new HashMap<>();
		serializers.put(CmModel.content.content, serializer);
		return create(node, serializers);
	}
	@Override
	public NodeReference create(BusinessNode node, Map<ContentPropertyModel, RepositoryContentSerializer<?>> serializers) throws DuplicateChildNameException {
		return create(Collections.singletonList(node), serializers).get(0);
	}
	@Override
	public List<NodeReference> create(List<BusinessNode> nodes) throws DuplicateChildNameException {
		List<RepositoryNode> repoNodes = new ArrayList<>();
		for (BusinessNode node : nodes) {
			repoNodes.add(node.getRepositoryNode());
		}
		return nodeService.create(repoNodes);
	}
	@Override
	public List<NodeReference> create(List<BusinessNode> nodes, Map<ContentPropertyModel, RepositoryContentSerializer<?>> serializers) throws DuplicateChildNameException {
		Map<NameReference, RepositoryContentSerializer<?>> serializers2 = new HashMap<>();
		for (Entry<ContentPropertyModel, RepositoryContentSerializer<?>> entry : serializers.entrySet()) {
			serializers2.put(entry.getKey().getNameReference(), entry.getValue());
		}
		List<RepositoryNode> repoNodes = new ArrayList<>();
		for (BusinessNode node : nodes) {
			repoNodes.add(node.getRepositoryNode());
		}
		return nodeService.create(repoNodes, serializers2);
	}

	@Override
	public void update(BusinessNode node) throws DuplicateChildNameException {
		update(node, new NodeScopeBuilder()
				.fromNode(node));
	}

	@Override
	public void update(BusinessNode node, NodeScopeBuilder scope) throws DuplicateChildNameException {
		nodeService.update(node.getRepositoryNode(), scope.getScope());
	}
	@Override
	public void update(List<BusinessNode> nodes, NodeScopeBuilder nodeScopeBuilder) throws DuplicateChildNameException {
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
