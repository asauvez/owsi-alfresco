package fr.openwide.alfresco.component.model.node.service.impl;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.search.util.BusinessNode;
import fr.openwide.alfresco.component.model.search.util.NodeFetchDetailsBuilder;
import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class NodeModelServiceImpl implements NodeModelService {

	@Autowired
	private NodeService nodeService;

	@Override
	public NodeReference createFolder(NodeReference parentRef, String folderName) throws DuplicateChildNameException {
		return create(new BusinessNode()
			.primaryParentRef(parentRef)
			.type(CmModel.folder)
			.name(folderName));
	}

	@Override
	public NodeReference createContent(NodeReference parentRef, String fileName, String mimeType, String encoding, Resource content) throws DuplicateChildNameException {
		return create(new BusinessNode()
				.primaryParentRef(parentRef)
				.type(CmModel.content)
				.name(fileName)
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
		return createContent(parent, file.getOriginalFilename(), file.getContentType(), null, resource);
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
