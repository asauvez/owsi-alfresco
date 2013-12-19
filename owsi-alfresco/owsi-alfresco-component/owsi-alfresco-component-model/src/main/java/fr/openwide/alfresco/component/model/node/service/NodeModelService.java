package fr.openwide.alfresco.component.model.node.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import fr.openwide.alfresco.component.model.search.util.BusinessNode;
import fr.openwide.alfresco.component.model.search.util.NodeFetchDetailsBuilder;
import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public interface NodeModelService {

	NodeReference createFolder(NodeReference parent, String folderName) throws DuplicateChildNameException;

	NodeReference createContent(NodeReference parent, String fileName, String mimeType, String encoding, InputStream content) throws DuplicateChildNameException;

	NodeReference createContent(NodeReference parent, MultipartFile file) throws DuplicateChildNameException, IOException;

	NodeReference create(BusinessNode node) throws DuplicateChildNameException;
	
	NodeReference create(BusinessNode node, InputStream content) throws DuplicateChildNameException;

	void update(BusinessNode node, NodeFetchDetailsBuilder details);
	
	void update(BusinessNode node, NodeFetchDetailsBuilder details, InputStream content);

	void delete(NodeReference nodeReference);

}
