package fr.openwide.alfresco.app.core.node.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResponseExtractor;

import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

@Service
public class NodeServiceImpl implements NodeService {

	@Autowired
	private RepositoryRemoteBinding repositoryRemoteBinding;

	@Override
	public RepositoryNode get(NodeReference nodeReference, NodeScope nodeScope) {
		GET_NODE_SERVICE request = new GET_NODE_SERVICE();
		request.nodeReference = nodeReference;
		request.nodeScope = nodeScope;
		
		return repositoryRemoteBinding.builder(GET_NODE_SERVICE.ENDPOINT)
				.headerPayload(request)
				.call();
	}

	@Override
	public void getNodeContent(NodeReference nodeReference, NameReference property, ResponseExtractor<?> responseExtractor) {
		repositoryRemoteBinding.builder(GET_NODE_CONTENT_ENDPOINT)
			.urlVariable((property != null) ? ";" + property.getFullName() : "")
			.urlVariable(nodeReference)
			.call(responseExtractor);
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
	public List<RepositoryNode> getChildren(NodeReference nodeReference, NameReference childAssocTypeName, NodeScope nodeScope) {
		CHILDREN_NODE_SERVICE request = new CHILDREN_NODE_SERVICE();
		request.nodeReference = nodeReference;
		request.childAssocTypeName = childAssocTypeName;
		request.nodeScope = nodeScope;
		
		return repositoryRemoteBinding.builder(CHILDREN_NODE_SERVICE.ENDPOINT)
				.headerPayload(request)
				.call();
	}

	@Override
	public List<RepositoryNode> getTargetAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope) {
		TARGET_ASSOC_NODE_SERVICE request = new TARGET_ASSOC_NODE_SERVICE();
		request.nodeReference = nodeReference;
		request.assocName = assocName;
		request.nodeScope = nodeScope;
		
		return repositoryRemoteBinding.builder(TARGET_ASSOC_NODE_SERVICE.ENDPOINT)
				.headerPayload(request)
				.call();
	}

	@Override
	public List<RepositoryNode> getSourceAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope) {
		SOURCE_ASSOC_NODE_SERVICE request = new SOURCE_ASSOC_NODE_SERVICE();
		request.nodeReference = nodeReference;
		request.assocName = assocName;
		request.nodeScope = nodeScope;

		return repositoryRemoteBinding.builder(SOURCE_ASSOC_NODE_SERVICE.ENDPOINT)
				.headerPayload(request)
				.call();
	}

	@Override
	public NodeReference create(RepositoryNode node) throws DuplicateChildNameException {
		CREATE_NODE_SERVICE request = new CREATE_NODE_SERVICE();
		request.node = node;
		request.contentBodyProperty = getContentBodyProperty(node);
		
		return repositoryRemoteBinding.builder(CREATE_NODE_SERVICE.ENDPOINT, getContent(node))
				.headerPayload(request)
				.call();
	}

	@Override
	public void update(RepositoryNode node, NodeScope nodeScope) throws DuplicateChildNameException {
		UPDATE_NODE_SERVICE request = new UPDATE_NODE_SERVICE();
		request.node = node;
		request.nodeScope = nodeScope;
		request.contentBodyProperty = getContentBodyProperty(node);
		
		repositoryRemoteBinding.builder(UPDATE_NODE_SERVICE.ENDPOINT, getContent(node))
				.headerPayload(request)
				.call();
	}

	private String getContentBodyProperty(RepositoryNode node) {
		Iterator<NameReference> itResources = node.getContentResources().keySet().iterator();
		if (! itResources.hasNext()) {
			return null;
		}
		NameReference property = itResources.next();
		if (itResources.hasNext()) {
			throw new IllegalArgumentException("Service does not support more than one contentResource upload at a time");
		}
		return property.getFullName();
	}
	private Resource getContent(RepositoryNode node) {
		Iterator<Resource> itResources = node.getContentResources().values().iterator();
		return (itResources.hasNext()) ? itResources.next() : null;
	}

	@Override
	public void delete(NodeReference nodeReference) {
		repositoryRemoteBinding.builder(DELETE_NODE_SERVICE_ENDPOINT)
			.headerPayload(nodeReference)
			.call();
	}

}
