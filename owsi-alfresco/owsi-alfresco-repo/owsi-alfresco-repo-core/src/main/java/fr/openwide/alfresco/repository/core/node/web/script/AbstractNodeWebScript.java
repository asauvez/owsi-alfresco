package fr.openwide.alfresco.repository.core.node.web.script;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.cmr.repository.ContentReader;
import org.apache.commons.io.IOUtils;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentDeserializer;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentSerializationUtils;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentSerializer;
import fr.openwide.alfresco.repository.api.node.model.ContentPropertyWrapper;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService;
import fr.openwide.alfresco.repository.api.remote.exception.IllegalStateRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.RestEndpoint;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractParameterRemoteWebScript;

public abstract class AbstractNodeWebScript<R, P> extends AbstractParameterRemoteWebScript<R, P> {

	protected NodeRemoteService nodeService;

	protected abstract R execute(P payload);

	protected Collection<RepositoryNode> getInputNodes(P payload) {
		return null;
	}
	protected Collection<RepositoryNode> getOutputNodes(R result) {
		return null;
	}

	@Override
	protected R executeImpl(P payload, WebScriptRequest request) {
		Collection<RepositoryNode> inputNodes = getInputNodes(payload);
		
		Map<Integer, ContentPropertyWrapper> wrappers = (inputNodes != null) ? deserializeContentExtensions(inputNodes) : null;
		
		R result = execute(payload);
		
		if (wrappers != null) {
			// Ecrit les contents uploadés dans Alfresco
			deserializeContentData(wrappers, request);
		}
		return result;
	}

	@Override
	protected void handleResult(WebScriptResponse response, R result) throws IOException {
		Collection<RepositoryNode> outputNodes = getOutputNodes(result);
		if (outputNodes == null) {
			super.handleResult(response, result);
		} else {
			RepositoryContentSerializationUtils.serializeContentExtensions(outputNodes);
			
			String valueHeader = objectMapper.writeValueAsString(result);
			response.setHeader(RestEndpoint.HEADER_MESSAGE_CONTENT, URLEncoder.encode(valueHeader, StandardCharsets.UTF_8.name()));
			
			serializeContent(outputNodes, response);
		}
	}

	public static class ContentCallback {
		public void doWithInputStream(NameReference contentProperty, InputStream inputStream) {}
	}

	private void serializeContent(Collection<RepositoryNode> nodes, WebScriptResponse response) throws IOException {
		// register ContentReader serializer
		Map<Class<?>, RepositoryContentSerializer<?>> serializersByClass = new HashMap<>();
		serializersByClass.put(ContentReader.class, new RepositoryContentSerializer<ContentReader>() {
			@Override
			public void serialize(RepositoryNode node, NameReference contentProperty, ContentReader ContentReader, OutputStream outputStream) throws IOException {
				try (InputStream inputStream = ContentReader.getContentInputStream()) {
					IOUtils.copy(inputStream, outputStream);
				}
			}
		});
		// generate output
		response.setContentType(RepositoryContentSerializationUtils.CONTENT_TYPE);
		RepositoryContentSerializationUtils.serializeContent(
				nodes, 
				new HashMap<NameReference, RepositoryContentSerializer<?>>(), 
				serializersByClass, 
				response.getOutputStream());
	}

	/** 
	 * Pré-traitement : initialise la map de content avec un callback noop écrasé
	 */
	private Map<Integer, ContentPropertyWrapper> deserializeContentExtensions(Collection<RepositoryNode> nodes) {
		Map<Integer, ContentPropertyWrapper> wrappers = RepositoryContentSerializationUtils.deserializeContentExtensions(nodes);
		for (ContentPropertyWrapper wrapper : wrappers.values()) {
			wrapper.getNode().getContents().put(wrapper.getContentProperty(), new ContentCallback());
		}
		return wrappers;
	}

	/** 
	 * Post-traitement : lit le zip de content et appelle les callbacks
	 */
	private void deserializeContentData(Map<Integer, ContentPropertyWrapper> wrappers, WebScriptRequest request) {
		RepositoryContentDeserializer<Void> defaultDeserializer = new RepositoryContentDeserializer<Void>() {
			@Override
			public Void deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
				ContentCallback callback = (ContentCallback) node.getContents().get(contentProperty);
				callback.doWithInputStream(contentProperty, inputStream);
				return null;
			}
		};
		try {
			RepositoryContentSerializationUtils.deserializeContent(wrappers, 
					new HashMap<NameReference, RepositoryContentDeserializer<?>>(), 
					defaultDeserializer, 
					request.getContent().getInputStream());
		} catch (IOException e) {
			throw new IllegalStateRemoteException(e);
		}
	}

	public void setNodeService(NodeRemoteService nodeService) {
		this.nodeService = nodeService;
	}

}
