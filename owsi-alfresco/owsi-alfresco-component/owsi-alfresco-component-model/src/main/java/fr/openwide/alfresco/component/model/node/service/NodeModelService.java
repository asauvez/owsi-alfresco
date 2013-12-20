package fr.openwide.alfresco.component.model.node.service;

import java.io.IOException;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import fr.openwide.alfresco.component.model.search.util.BusinessNode;
import fr.openwide.alfresco.component.model.search.util.NodeFetchDetailsBuilder;
import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public interface NodeModelService {

	NodeReference createFolder(NodeReference parent, String folderName) throws DuplicateChildNameException;

	NodeReference createContent(NodeReference parent, String fileName, String mimeType, String encoding, Resource content) throws DuplicateChildNameException;

	NodeReference createContent(NodeReference parent, MultipartFile file) throws DuplicateChildNameException, IOException;

	NodeReference create(BusinessNode node) throws DuplicateChildNameException;
	
	NodeReference create(BusinessNode node, Resource content) throws DuplicateChildNameException;

	void update(BusinessNode node, NodeFetchDetailsBuilder details);
	
	void update(BusinessNode node, NodeFetchDetailsBuilder details, Resource content);

	void delete(NodeReference nodeReference);

}
