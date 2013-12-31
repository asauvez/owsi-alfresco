package fr.openwide.alfresco.app.core.node.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseExtractor;

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
	public RepositoryNode get(NodeReference nodeReference, NodeFetchDetails nodeFetchDetails) {
		try {
			GET_NODE_SERVICE request = new GET_NODE_SERVICE();
			request.nodeReference = nodeReference;
			request.nodeFetchDetails = nodeFetchDetails;
			HttpHeaders headers = payloadParameterHandler.handlePayload(request);
			return repositoryRemoteBinding.exchange(GET_NODE_SERVICE.URL, 
					GET_NODE_SERVICE.METHOD, request, RepositoryNode.class, headers);
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void getNodeContent(NodeReference nodeReference, NameReference property, ResponseExtractor<?> responseExtractor) {
		Matcher matcher = NodeReference.PATTERN.matcher(nodeReference.getReference());
		matcher.matches();
		repositoryRemoteBinding.getRequestContent(GET_NODE_CONTENT_SERVICE.URL, GET_NODE_CONTENT_SERVICE.METHOD, 
				responseExtractor, 
				(property != null) ? ";" + property.getFullName() : "",
				matcher.group(1), matcher.group(2), matcher.group(3));
	}

	@Override
	public RepositoryContentData getNodeContent(NodeReference nodeReference, NameReference property, final OutputStream out) {
		final RepositoryContentData contentData = new RepositoryContentData();
		getNodeContent(nodeReference, property, new ResponseExtractor<Void>() {
			@Override
			public Void extractData(ClientHttpResponse response) throws IOException {
				HttpHeaders headers = response.getHeaders();
				contentData.setMimetype(headers.getContentType().toString());
				contentData.setSize(headers.getContentLength());
				IOUtils.copy(response.getBody(), out);
				return null;
			}
		});
		return contentData;
	}

	@Override
	public List<RepositoryNode> getChildren(NodeReference nodeReference, NameReference childAssocTypeName, NodeFetchDetails nodeFetchDetails) {
		try {
			CHILDREN_NODE_SERVICE request = new CHILDREN_NODE_SERVICE();
			request.nodeReference = nodeReference;
			request.childAssocTypeName = childAssocTypeName;
			request.nodeFetchDetails = nodeFetchDetails;
			HttpHeaders headers = payloadParameterHandler.handlePayload(request);
			return repositoryRemoteBinding.exchangeCollection(CHILDREN_NODE_SERVICE.URL, 
					CHILDREN_NODE_SERVICE.METHOD, request, new ParameterizedTypeReference<List<RepositoryNode>>() {}, headers);
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

	@Override
	public List<RepositoryNode> getTargetAssocs(NodeReference nodeReference, NameReference assocName, NodeFetchDetails nodeFetchDetails) {
		try {
			TARGET_ASSOC_NODE_SERVICE request = new TARGET_ASSOC_NODE_SERVICE();
			request.nodeReference = nodeReference;
			request.assocName = assocName;
			request.nodeFetchDetails = nodeFetchDetails;
			HttpHeaders headers = payloadParameterHandler.handlePayload(request);
			return repositoryRemoteBinding.exchangeCollection(TARGET_ASSOC_NODE_SERVICE.URL, 
					TARGET_ASSOC_NODE_SERVICE.METHOD, request, new ParameterizedTypeReference<List<RepositoryNode>>() {}, headers);
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

	@Override
	public List<RepositoryNode> getSourceAssocs(NodeReference nodeReference, NameReference assocName, NodeFetchDetails nodeFetchDetails) {
		try {
			SOURCE_ASSOC_NODE_SERVICE request = new SOURCE_ASSOC_NODE_SERVICE();
			request.nodeReference = nodeReference;
			request.assocName = assocName;
			request.nodeFetchDetails = nodeFetchDetails;
			HttpHeaders headers = payloadParameterHandler.handlePayload(request);
			return repositoryRemoteBinding.exchangeCollection(SOURCE_ASSOC_NODE_SERVICE.URL, 
					SOURCE_ASSOC_NODE_SERVICE.METHOD, request, new ParameterizedTypeReference<List<RepositoryNode>>() {}, headers);
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

	@Override
	public NodeReference create(RepositoryNode node) throws DuplicateChildNameException {
		try {
			HttpHeaders headers = payloadParameterHandler.handlePayload(node);
			return repositoryRemoteBinding.exchange(CREATE_NODE_SERVICE.URL, 
					CREATE_NODE_SERVICE.METHOD, 
					getContent(node, headers),
					NodeReference.class, headers);
		} catch (DuplicateChildNameException e) {
			throw e;
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void update(RepositoryNode node, NodeFetchDetails details) {
		try {
			UPDATE_NODE_SERVICE request = new UPDATE_NODE_SERVICE();
			request.node = node;
			request.details = details;
			HttpHeaders headers = payloadParameterHandler.handlePayload(request);
			
			repositoryRemoteBinding.exchange(UPDATE_NODE_SERVICE.URL, 
					UPDATE_NODE_SERVICE.METHOD, 
					getContent(node, headers), 
					Void.class, headers);
		} catch (RepositoryRemoteException e) {
			// do not deal with other types of remote exception
			throw new IllegalStateException(e);
		}
	}

	private Resource getContent(RepositoryNode node, HttpHeaders headers) {
		Iterator<Entry<NameReference, Resource>> itResources = node.getContentResources().entrySet().iterator();
		if (! itResources.hasNext()) {
			return null;
		}
		Entry<NameReference, Resource> entry = itResources.next();
		headers.add(CREATE_NODE_SERVICE.CONTENT_PROPERTY_HEADER, entry.getKey().getFullName());
		Resource content = entry.getValue();
		if (itResources.hasNext()) {
			throw new IllegalArgumentException("Service does not support more than one contentResource upload at a time");
		}
		return content;
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
