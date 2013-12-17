package fr.openwide.alfresco.app.core.node.service;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.web.multipart.MultipartFile;

import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public interface NodeService extends NodeRemoteService {

	NodeReference createFolder(NodeReference parent, String folderName) throws DuplicateChildNameException;

	NodeReference createContent(NodeReference parent, String fileName, String mimeType, String encoding, InputStream content) throws DuplicateChildNameException;

	NodeReference createContent(NodeReference parent, MultipartFile file) throws DuplicateChildNameException, IOException;
}
