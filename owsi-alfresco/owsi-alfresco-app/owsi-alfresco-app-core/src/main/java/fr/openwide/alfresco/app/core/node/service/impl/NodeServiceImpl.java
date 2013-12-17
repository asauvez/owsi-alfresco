package fr.openwide.alfresco.app.core.node.service.impl;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.CharEncoding;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryPayloadParameterHandler;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

@Service
public class NodeServiceImpl implements NodeService {

	@Autowired
	private RepositoryRemoteBinding repositoryRemoteBinding;

	@Autowired
	private RepositoryPayloadParameterHandler payloadParameterHandler;

	@Override
	public NodeReference createFolder(NodeReference parentRef, String folderName) throws DuplicateChildNameException {
		RepositoryNode node = new RepositoryNode();
		node.setPrimaryParent(new RepositoryNode(parentRef));
		node.setType(NameReference.create("cm", "folder"));
		node.getProperties().put(NameReference.create("cm", "name"), folderName);
		return create(node, null);
	}

	@Override
	public NodeReference createContent(NodeReference parentRef, String fileName, String mimeType, String encoding, InputStream content) throws DuplicateChildNameException {
		RepositoryNode node = new RepositoryNode();
		node.setPrimaryParent(new RepositoryNode(parentRef));
		node.setType(NameReference.create("cm", "content"));
		node.getProperties().put(NameReference.create("cm", "name"), fileName);
		
		RepositoryContentData contentData = new RepositoryContentData();
		contentData.setMimetype(mimeType);
		node.getProperties().put(NameReference.create("cm", "content"), contentData);
		return create(node, content);
	}

	@Override
	public NodeReference createContent(NodeReference parent, MultipartFile file) throws DuplicateChildNameException, IOException {
		return createContent(parent, file.getOriginalFilename(), file.getContentType(), CharEncoding.UTF_8, file.getInputStream());
	}

	@Override
	public NodeReference create(RepositoryNode node, InputStream content) throws DuplicateChildNameException {
		try {
			HttpHeaders headers = payloadParameterHandler.handlePayload(node);
			return repositoryRemoteBinding.exchange(CREATE_NODE_SERVICE.URL, 
					CREATE_NODE_SERVICE.METHOD, 
					(content != null) ? new InputStreamResource(content) : null,
					NodeReference.class, headers);
		} catch (DuplicateChildNameException e) {
			throw e;
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void update(RepositoryNode node, NodeFetchDetails details, InputStream content) {
		try {
			UPDATE_NODE_SERVICE request = new UPDATE_NODE_SERVICE();
			request.node = node;
			request.details = details;
			HttpHeaders headers = payloadParameterHandler.handlePayload(request);
			
			repositoryRemoteBinding.exchange(UPDATE_NODE_SERVICE.URL, 
					UPDATE_NODE_SERVICE.METHOD, 
					(content != null) ? new InputStreamResource(content) : null, 
					Void.class, headers);
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void delete(NodeReference nodeReference) {
		try {
			HttpHeaders headers = payloadParameterHandler.handlePayload(nodeReference);
			repositoryRemoteBinding.exchange(DELETE_NODE_SERVICE.URL, 
					DELETE_NODE_SERVICE.METHOD, (Object) null, Void.class, headers);
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

}
