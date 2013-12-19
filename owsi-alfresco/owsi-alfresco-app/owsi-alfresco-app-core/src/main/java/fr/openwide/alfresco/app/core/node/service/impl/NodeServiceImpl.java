package fr.openwide.alfresco.app.core.node.service.impl;

import java.io.InputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryPayloadParameterHandler;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

@Service
public class NodeServiceImpl implements NodeService {

	@Autowired
	private RepositoryRemoteBinding repositoryRemoteBinding;

	@Autowired
	private RepositoryPayloadParameterHandler payloadParameterHandler;

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
