package fr.openwide.alfresco.app.core.node.service.impl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.type.TypeReference;

import fr.openwide.alfresco.app.core.node.serializer.ByteArrayRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.serializer.InputStreamRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.serializer.MultipartFileRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.serializer.StringRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.serializer.TempFileRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.service.NodeService;
import fr.openwide.alfresco.app.core.remote.model.RestCallBuilder;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.serializer.RepositoryContentDeserializer;
import fr.openwide.alfresco.repository.api.node.serializer.RepositoryContentSerializer;
import fr.openwide.alfresco.repository.api.node.serializer.RepositoryContentSerializerUtils;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

@Service
public class NodeServiceImpl implements NodeService {

	private static final RepositoryContentDeserializer<?> DEFAULT_REPOSITORY_CONTENT_DESERIALIZER = ByteArrayRepositoryContentSerializer.INSTANCE;
	private static final Map<Class<?>, RepositoryContentSerializer<?>> SERIALIZERS_BY_CLASS = new HashMap<>();
	static {
		SERIALIZERS_BY_CLASS.put(String.class, StringRepositoryContentSerializer.INSTANCE);
		SERIALIZERS_BY_CLASS.put(byte[].class, ByteArrayRepositoryContentSerializer.INSTANCE);
		SERIALIZERS_BY_CLASS.put(File.class, TempFileRepositoryContentSerializer.INSTANCE);
		SERIALIZERS_BY_CLASS.put(MultipartFile.class, MultipartFileRepositoryContentSerializer.INSTANCE);
		SERIALIZERS_BY_CLASS.put(InputStream.class, InputStreamRepositoryContentSerializer.INSTANCE);
	}
	
	@Autowired
	private RepositoryRemoteBinding repositoryRemoteBinding;

	@Override
	public RepositoryNode get(NodeReference nodeReference, final NodeScope nodeScope) {
		GET_NODE_SERVICE request = new GET_NODE_SERVICE();
		request.nodeReference = nodeReference;
		request.nodeScope = nodeScope;
		
		return repositoryRemoteBinding.builder(GET_NODE_SERVICE.ENDPOINT)
				.headerPayload(request)
				.call(new ResponseExtractor<RepositoryNode>() {
					@Override
					public RepositoryNode extractData(ClientHttpResponse response) throws IOException {
						RepositoryNode node = RestCallBuilder.getHeaderPayload(response, RepositoryNode.class);
						RepositoryContentSerializerUtils.deserialize(
								Collections.singleton(node), 
								nodeScope.getContentDeserializers(), 
								DEFAULT_REPOSITORY_CONTENT_DESERIALIZER, 
								response.getBody());
						return node;
					}
				});
	}

	@Override
	public void getNodeContent(NodeReference nodeReference, NameReference property, ResponseExtractor<Void> responseExtractor) {
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

	public static class NodeListResponseExtractor implements ResponseExtractor<List<RepositoryNode>> {
		private final Map<NameReference, RepositoryContentDeserializer<?>> deserializers;

		public NodeListResponseExtractor(Map<NameReference, RepositoryContentDeserializer<?>> deserializers) {
			this.deserializers = deserializers;
		}
		
		@Override
		public List<RepositoryNode> extractData(ClientHttpResponse response) throws IOException {
			List<RepositoryNode> nodes = RestCallBuilder.getHeaderPayload(response, new TypeReference<List<RepositoryNode>>() {});
			RepositoryContentSerializerUtils.deserialize(nodes, deserializers, DEFAULT_REPOSITORY_CONTENT_DESERIALIZER, response.getBody());
			return nodes;
		}
	}
	
	@Override
	public List<RepositoryNode> getChildren(NodeReference nodeReference, NameReference childAssocTypeName, NodeScope nodeScope) {
		CHILDREN_NODE_SERVICE request = new CHILDREN_NODE_SERVICE();
		request.nodeReference = nodeReference;
		request.childAssocTypeName = childAssocTypeName;
		request.nodeScope = nodeScope;
		
		return repositoryRemoteBinding.builder(CHILDREN_NODE_SERVICE.ENDPOINT)
				.headerPayload(request)
				.call(new NodeListResponseExtractor(nodeScope.getContentDeserializers()));
	}

	@Override
	public List<RepositoryNode> getTargetAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope) {
		TARGET_ASSOC_NODE_SERVICE request = new TARGET_ASSOC_NODE_SERVICE();
		request.nodeReference = nodeReference;
		request.assocName = assocName;
		request.nodeScope = nodeScope;
		
		return repositoryRemoteBinding.builder(TARGET_ASSOC_NODE_SERVICE.ENDPOINT)
				.headerPayload(request)
				.call(new NodeListResponseExtractor(nodeScope.getContentDeserializers()));
	}

	@Override
	public List<RepositoryNode> getSourceAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope) {
		SOURCE_ASSOC_NODE_SERVICE request = new SOURCE_ASSOC_NODE_SERVICE();
		request.nodeReference = nodeReference;
		request.assocName = assocName;
		request.nodeScope = nodeScope;

		return repositoryRemoteBinding.builder(SOURCE_ASSOC_NODE_SERVICE.ENDPOINT)
				.headerPayload(request)
				.call(new NodeListResponseExtractor(nodeScope.getContentDeserializers()));
	}

	@Override
	public NodeReference create(RepositoryNode node) throws DuplicateChildNameException {
		return create(node, new HashMap<NameReference, RepositoryContentSerializer<?>>());
	}

	@Override
	public List<NodeReference> create(List<RepositoryNode> nodes) throws DuplicateChildNameException {
		return create(nodes, new HashMap<NameReference, RepositoryContentSerializer<?>>());
	}
	
	@Override
	public NodeReference create(RepositoryNode node, Map<NameReference, RepositoryContentSerializer<?>> serializers) throws DuplicateChildNameException {
		List<NodeReference> list = create(Collections.singletonList(node), serializers);
		return list.get(0);
	}
	
	@Override
	public List<NodeReference> create(final List<RepositoryNode> nodes, final Map<NameReference, RepositoryContentSerializer<?>> serializers) throws DuplicateChildNameException {
		CREATE_NODE_SERVICE request = new CREATE_NODE_SERVICE();
		request.nodes = nodes;
		
		RepositoryContentSerializerUtils.serializeProperties(nodes);
		
		return repositoryRemoteBinding.builder(CREATE_NODE_SERVICE.ENDPOINT)
			.headerPayload(request)
			.header("Content-Type", RepositoryContentSerializerUtils.CONTENT_TYPE)
			.call(new RequestCallback() {
				@Override
				public void doWithRequest(ClientHttpRequest request) throws IOException {
					RepositoryContentSerializerUtils.serializeContent(nodes, 
							serializers, 
							SERIALIZERS_BY_CLASS, 
							request.getBody());
				}
			});
	}
	
	@Override
	public void update(List<RepositoryNode> nodes, NodeScope nodeScope) throws DuplicateChildNameException {
		update(nodes, nodeScope, new HashMap<NameReference, RepositoryContentSerializer<?>>());
	}
	@Override
	public void update(RepositoryNode node, NodeScope nodeScope) throws DuplicateChildNameException {
		update(node, nodeScope, new HashMap<NameReference, RepositoryContentSerializer<?>>());
	}
	@Override
	public void update(RepositoryNode node, NodeScope nodeScope,
			Map<NameReference, RepositoryContentSerializer<?>> serializers) throws DuplicateChildNameException {
		update(Collections.singletonList(node), nodeScope, serializers);
	}

	@Override
	public void update(final List<RepositoryNode> nodes, final NodeScope nodeScope, 
			final Map<NameReference, RepositoryContentSerializer<?>> serializers) throws DuplicateChildNameException {
		UPDATE_NODE_SERVICE request = new UPDATE_NODE_SERVICE();
		request.nodes = nodes;
		request.nodeScope = nodeScope;
		
		RepositoryContentSerializerUtils.serializeProperties(nodes);
		
		repositoryRemoteBinding.builder(UPDATE_NODE_SERVICE.ENDPOINT)
			.headerPayload(request)
			.header("Content-Type", MediaType.APPLICATION_OCTET_STREAM_VALUE)
			.call(new RequestCallback() {
				@Override
				public void doWithRequest(ClientHttpRequest request) throws IOException {
					RepositoryContentSerializerUtils.serializeContent(nodes, 
							serializers, 
							SERIALIZERS_BY_CLASS, 
							request.getBody());
				}
			});
	}
	
	@Override
	public void delete(NodeReference nodeReference) {
		repositoryRemoteBinding.builder(DELETE_NODE_SERVICE_ENDPOINT)
			.headerPayload(nodeReference)
			.call();
	}

}
