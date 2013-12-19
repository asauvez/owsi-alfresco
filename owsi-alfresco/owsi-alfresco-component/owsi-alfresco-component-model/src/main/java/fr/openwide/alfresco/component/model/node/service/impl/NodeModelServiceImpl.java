package fr.openwide.alfresco.component.model.node.service.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
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
			.property(CmModel.folder.name, folderName));
	}

	@Override
	public NodeReference createContent(NodeReference parentRef, String fileName, String mimeType, String encoding, InputStream content) throws DuplicateChildNameException {
		RepositoryContentData contentData = new RepositoryContentData();
		contentData.setMimetype(mimeType);
		contentData.setEncoding(encoding);
		
		return create(new BusinessNode()
			.primaryParentRef(parentRef)
			.type(CmModel.content)
			.property(CmModel.content.name, fileName)
			.property(CmModel.content.content, contentData));
	}

	@Override
	public NodeReference createContent(NodeReference parent, MultipartFile file) throws DuplicateChildNameException, IOException {
		return createContent(parent, file.getOriginalFilename(), file.getContentType(), CharEncoding.UTF_8, file.getInputStream());
	}

	@Override
	public NodeReference create(BusinessNode node) throws DuplicateChildNameException {
		return create(node, null);
	}
	
	@Override
	public NodeReference create(BusinessNode node, InputStream content) throws DuplicateChildNameException {
		return nodeService.create(node.getRepositoryNode(), content);
	}

	@Override
	public void update(BusinessNode node, NodeFetchDetailsBuilder details) {
		update(node, details, null);
	}

	@Override
	public void update(BusinessNode node, NodeFetchDetailsBuilder details, InputStream content) {
		nodeService.update(node.getRepositoryNode(), details.getDetails(), content);
	}

	@Override
	public void delete(NodeReference nodeReference) {
		nodeService.delete(nodeReference);
	}

}
