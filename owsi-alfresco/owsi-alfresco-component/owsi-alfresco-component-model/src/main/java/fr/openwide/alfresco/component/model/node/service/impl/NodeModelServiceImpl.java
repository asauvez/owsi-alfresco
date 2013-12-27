package fr.openwide.alfresco.component.model.node.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.component.model.node.model.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.NodeFetchDetailsBuilder;
import fr.openwide.alfresco.component.model.node.model.property.ContentPropertyModel;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class NodeModelServiceImpl implements NodeModelService {

	@Autowired
	private NodeService nodeService;

	@Override
	public BusinessNode get(NodeReference nodeReference, NodeFetchDetailsBuilder nodeFetchDetails) {
		RepositoryNode node = nodeService.get(nodeReference, nodeFetchDetails.getDetails());
		return (node != null) ? new BusinessNode(node) : null;
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
	public List<BusinessNode> getChildren(NodeReference nodeReference, NodeFetchDetailsBuilder nodeFetchDetails) {
		return getChildren(nodeReference, CmModel.folder.contains, nodeFetchDetails);
	}
	
	@Override
	public List<BusinessNode> getChildren(NodeReference nodeReference, ChildAssociationModel childAssoc, NodeFetchDetailsBuilder nodeFetchDetails) {
		return BusinessNode.wrapList(nodeService.getChildren(nodeReference, childAssoc.getNameReference(), nodeFetchDetails.getDetails()));
	}

	@Override
	public List<BusinessNode> getTargetAssocs(NodeReference nodeReference, AssociationModel assoc, NodeFetchDetailsBuilder nodeFetchDetails) {
		return BusinessNode.wrapList(nodeService.getTargetAssocs(nodeReference, assoc.getNameReference(), nodeFetchDetails.getDetails()));
	}

	@Override
	public List<BusinessNode> getSourceAssocs(NodeReference nodeReference, AssociationModel assoc, NodeFetchDetailsBuilder nodeFetchDetails) {
		return BusinessNode.wrapList(nodeService.getSourceAssocs(nodeReference, assoc.getNameReference(), nodeFetchDetails.getDetails()));
	}
	
	@Override
	public NodeReference createFolder(NodeReference parentRef, String folderName) throws DuplicateChildNameException {
		return create(new BusinessNode(parentRef, CmModel.folder, folderName));
	}

	@Override
	public NodeReference createContent(NodeReference parentRef, String fileName, String mimeType, String encoding, Resource content) throws DuplicateChildNameException {
		return create(new BusinessNode(parentRef, CmModel.content, fileName)
				.property(CmModel.content.content, new RepositoryContentData(mimeType, encoding)),
			content);
	}

	@Override
	public NodeReference createContent(NodeReference parent, final MultipartFile file) throws DuplicateChildNameException, IOException {
		Resource resource = new InputStreamResource(file.getInputStream()) {
			@Override
			public long contentLength() throws IOException {
				return file.getSize();
			}
		};
		String filename = FilenameUtils.getName(file.getOriginalFilename());
		return createContent(parent, filename, file.getContentType(), null, resource);
	}

	@Override
	public NodeReference create(BusinessNode node) throws DuplicateChildNameException {
		return create(node, null);
	}
	
	@Override
	public NodeReference create(BusinessNode node, Resource content) throws DuplicateChildNameException {
		return nodeService.create(node.getRepositoryNode(), content);
	}

	@Override
	public void update(BusinessNode node, NodeFetchDetailsBuilder details) {
		update(node, details, null);
	}

	@Override
	public void update(BusinessNode node, NodeFetchDetailsBuilder details, Resource content) {
		nodeService.update(node.getRepositoryNode(), details.getDetails(), content);
	}

	@Override
	public void delete(NodeReference nodeReference) {
		nodeService.delete(nodeReference);
	}

}
