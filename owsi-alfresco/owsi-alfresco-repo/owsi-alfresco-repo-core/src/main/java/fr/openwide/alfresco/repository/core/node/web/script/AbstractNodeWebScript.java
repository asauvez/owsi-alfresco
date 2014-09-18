package fr.openwide.alfresco.repository.core.node.web.script;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.repository.ContentReader;
import org.apache.commons.io.IOUtils;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.openwide.alfresco.repository.api.node.binding.NodePayloadCallback;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentDeserializer;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentSerializationComponent;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentSerializer;
import fr.openwide.alfresco.repository.api.node.model.ContentPropertyWrapper;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractRemoteWebScript;

public abstract class AbstractNodeWebScript<R, P> extends AbstractRemoteWebScript<R> {

	protected NodeRemoteService nodeService;
	private RepositoryContentSerializationComponent serializationComponent;

	protected abstract R execute(P payload);

	protected Collection<RepositoryNode> getInputNodes(@SuppressWarnings("unused") P payload) {
		return Collections.emptySet();
	}
	protected Collection<RepositoryNode> getOutputNodes(@SuppressWarnings("unused") R result) {
		return Collections.emptySet();
	}

	/**
	 * Provide {@link JavaType} used to unserialize the only argument. If null, body is not parsed and null is passed
	 * as the payload to {@link AbstractRemoteWebScript#executeImpl(WebScriptRequest, WebScriptResponse, Status, Cache)}
	 */
	protected abstract JavaType getParameterType();

	@Override
	protected R executeImpl(WebScriptRequest request, WebScriptResponse res, Status status, Cache cache) throws IOException {
		final List<R> resultList = new ArrayList<>();
		NodePayloadCallback<P> payloadCallback = new NodePayloadCallback<P>() {
			@Override
			public Collection<RepositoryNode> extractNodes(P payload) {
				return getInputNodes(payload);
			}
			@Override
			public void doWithPayload(P payload, Map<Integer, ContentPropertyWrapper> wrappers) {
				for (ContentPropertyWrapper wrapper : wrappers.values()) {
					wrapper.getNode().getContents().put(wrapper.getContentProperty(), new NodeContentCallback() {
						@Override
						public void doWithInputStream(NameReference contentProperty, InputStream inputStream) {
							// va être surchargé par le service
						}
					});
				}
				R result = execute(payload);
				resultList.add(result);
			}
		};
		
		serializationComponent.deserialize(
				getParameterType(), payloadCallback, null,
				request.getContent().getInputStream());
		
		return resultList.get(0);
	}

	@Override
	protected void handleResult(WebScriptResponse response, R result) throws IOException {
		Collection<RepositoryNode> outputNodes = getOutputNodes(result);
		// generate output
		response.setContentType(RepositoryContentSerializationComponent.CONTENT_TYPE);
		serializationComponent.serialize(result, outputNodes, null, response.getOutputStream());
	}

	public void setNodeService(NodeRemoteService nodeService) {
		this.nodeService = nodeService;
	}
	@Override
	public void setObjectMapper(ObjectMapper objectMapper) {
		super.setObjectMapper(objectMapper);
		
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
		
		RepositoryContentDeserializer<?> defaultDeserializer = new RepositoryContentDeserializer<Void>() {
			@Override
			public Void deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
				NodeContentCallback callback = (NodeContentCallback) node.getContents().get(contentProperty);
				callback.doWithInputStream(contentProperty, inputStream);
				return null;
			}
		};
		
		serializationComponent = new RepositoryContentSerializationComponent(objectMapper, serializersByClass, defaultDeserializer);
	}

}
