package fr.openwide.alfresco.repository.core.node.web.script;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;

import org.alfresco.service.cmr.repository.ContentReader;
import org.apache.commons.io.IOUtils;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.openwide.alfresco.api.core.node.binding.RemoteCallPayload;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentDeserializationParameters;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentDeserializer;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializationComponent;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializationParameters;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializer;
import fr.openwide.alfresco.api.core.node.binding.content.NodePayloadCallback;
import fr.openwide.alfresco.api.core.node.model.ContentPropertyWrapper;
import fr.openwide.alfresco.api.core.node.model.RemoteCallParameters;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractRemoteWebScript;

public abstract class AbstractNodeWebScript<R, P> extends AbstractRemoteWebScript<R> {

	private static NodeContentCallback NOOP_CONTENT_CALLBACK = new NodeContentCallback() {
		@Override
		public void doWithInputStream(NameReference contentProperty, InputStream inputStream) {
		}
	};
	
	protected NodeRemoteService nodeService;
	private NodeContentSerializationComponent serializationComponent;

	private NodeContentSerializationParameters defaultSerializationParameters = new NodeContentSerializationParameters();
	private NodeContentDeserializationParameters defaultDeserializationParameters = new NodeContentDeserializationParameters();

	protected abstract R execute(P payload);

	protected Collection<RepositoryNode> getInputNodes(@SuppressWarnings("unused") P payload) {
		return Collections.emptySet();
	}
	protected Collection<RepositoryNode> getOutputNodes(@SuppressWarnings("unused") R result) {
		return Collections.emptySet();
	}

	/**
	 * Provide {@link JavaType} used to unserialize the only argument. If null, body is not parsed and nu
			public Collection<RepositoryNode> extractNodes(P payload) {ll is passed
	 * as the payload to {@link AbstractRemoteWebScript#executeImpl(WebScriptRequest, WebScriptResponse, Status, Cache)}
	 */
	protected abstract JavaType getParameterType();

	@Override
	protected R executeImpl(WebScriptRequest request, final WebScriptResponse response, Status status, Cache cache) throws IOException {
		final AtomicReference<R> resultRef = new AtomicReference<>();
		NodePayloadCallback<P> payloadCallback = new NodePayloadCallback<P>() {
			@Override
			public Collection<RepositoryNode> extractNodes(P payload) {
				return getInputNodes(payload);
			}
			@Override
			public void doWithPayload(RemoteCallPayload<P> remoteCallPayload, Map<Integer, ContentPropertyWrapper> wrappers) {
				for (ContentPropertyWrapper wrapper : wrappers.values()) {
					// va être surchargé par le service
					wrapper.getNode().getContents().put(wrapper.getContentProperty(), NOOP_CONTENT_CALLBACK);
				}
				R result = execute(remoteCallPayload.getPayload());
				resultRef.set(result);
			}
		};
		
		RemoteCallPayload<P> remoteCallPayload = serializationComponent.deserialize(
				getParameterType(), payloadCallback, defaultDeserializationParameters,
				request.getContent().getInputStream());
		
		final R result = resultRef.get();
		
		// generate output
		response.setContentType(NodeContentSerializationComponent.CONTENT_TYPE);
		
		final Collection<RepositoryNode> outputNodes = getOutputNodes(result);
		RemoteCallParameters.execute(remoteCallPayload.getRemoteCallParameters(), new Callable<Void>() {
			@Override
			public Void call() throws IOException {
				try (OutputStream outputStream = response.getOutputStream()) {
					serializationComponent.serialize(result, outputNodes, defaultSerializationParameters, outputStream);
					return null;
				}
			}
		});
		
		return resultRef.get();
	}

	@Override
	protected void handleResult(WebScriptResponse response, R result) throws IOException {
		// noop : déjà traité plus haut
	}

	public void setNodeService(NodeRemoteService nodeService) {
		this.nodeService = nodeService;
	}
	@Override
	public void setObjectMapper(ObjectMapper objectMapper) {
		super.setObjectMapper(objectMapper);
		
		// register ContentReader serializer
		Map<Class<?>, NodeContentSerializer<?>> serializersByClass = new HashMap<>();
		serializersByClass.put(ContentReader.class, new NodeContentSerializer<ContentReader>() {
			@Override
			public void serialize(RepositoryNode node, NameReference contentProperty, ContentReader ContentReader, OutputStream outputStream) throws IOException {
				try (InputStream inputStream = ContentReader.getContentInputStream()) {
					IOUtils.copy(inputStream, outputStream);
				}
			}
		});
		
		NodeContentDeserializer<?> defaultDeserializer = new NodeContentDeserializer<Void>() {
			@Override
			public Void deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
				NodeContentCallback callback = (NodeContentCallback) node.getContents().get(contentProperty);
				callback.doWithInputStream(contentProperty, inputStream);
				return null;
			}
		};
		
		serializationComponent = new NodeContentSerializationComponent(objectMapper, serializersByClass, defaultDeserializer);
	}

}
