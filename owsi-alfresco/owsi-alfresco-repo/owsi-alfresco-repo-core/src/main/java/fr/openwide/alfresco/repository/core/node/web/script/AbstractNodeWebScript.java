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

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.ContentReader;
import org.apache.commons.io.IOUtils;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.openwide.alfresco.api.core.authentication.model.UserReference;
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
import fr.openwide.alfresco.api.core.remote.exception.InvalidMessageRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractRemoteWebScript;

public abstract class AbstractNodeWebScript<R, P> extends AbstractRemoteWebScript<RemoteCallPayload<R>, Content> {

	protected NodeRemoteService nodeService;
	private NodeContentSerializationComponent serializationComponent;

	private boolean runAsEnabled;

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
	protected Content extractPayload(WebScriptRequest req) {
		return req.getContent();
	}
	
	@Override
	protected RemoteCallPayload<R> executeImpl(Content payload) {
		final AtomicReference<R> resultRef = new AtomicReference<>();
		NodePayloadCallback<P> payloadCallback = new NodePayloadCallback<P>() {
			@Override
			public Collection<RepositoryNode> extractNodes(P payload) {
				return getInputNodes(payload);
			}
			@Override
			public void doWithPayload(final RemoteCallPayload<P> remoteCallPayload, Collection<ContentPropertyWrapper> wrappers) {
				for (ContentPropertyWrapper wrapper : wrappers) {
					// va être renseigné par le service
					wrapper.getNode().getContents().put(wrapper.getContentProperty(), new NodeContentHolder(wrapper));
				}
				// Vrai appel du Service
				UserReference runAs = remoteCallPayload.getRemoteCallParameters().getRunAs();
				R result;
				if (runAs == null) {
					result = execute(remoteCallPayload.getPayload());
				} else {
					if (! runAsEnabled) {
						throw new IllegalStateException("Can't specify a runAs userName because 'owsi-alfresco.run-as.enabled' is set to false.");
					}
					result = AuthenticationUtil.runAs(new RunAsWork<R>() {
						@Override
						public R doWork() throws Exception {
							return execute(remoteCallPayload.getPayload());
						}
					}, runAs.getUsername());
				}
				resultRef.set(result);
			}
		};
		
		try {
			// L'appel du service se fait dans le callback
			RemoteCallPayload<P> requestPayload = serializationComponent.deserialize(
					getParameterType(), payloadCallback, defaultDeserializationParameters,
					payload.getInputStream());
			return new RemoteCallPayload<R>(resultRef.get(), requestPayload.getRemoteCallParameters());
		} catch (IOException e) {
			throw new InvalidMessageRemoteException(e);
		}
		
	}

	@Override
	protected void handleResult(final WebScriptResponse response, final RemoteCallPayload<R> result) throws IOException {
		response.setContentType(NodeContentSerializationComponent.CONTENT_TYPE);
		final Collection<RepositoryNode> outputNodes = getOutputNodes(result.getPayload());
		RemoteCallParameters.execute(result.getRemoteCallParameters(), new Callable<Void>() {
			@Override
			public Void call() throws IOException {
				serializationComponent.serialize(result.getPayload(),
						outputNodes,
						defaultSerializationParameters,
						response.getOutputStream());
				return null;
			}
		});
	}

	@Override
	public void setObjectMapper(ObjectMapper objectMapper) {
		super.setObjectMapper(objectMapper);
		
		// register ContentReader serializer
		Map<Class<?>, NodeContentSerializer<?>> serializersByClass = new HashMap<>();
		serializersByClass.put(ContentReader.class, new NodeContentSerializer<ContentReader>() {
			@Override
			public void serialize(RepositoryNode node, NameReference contentProperty, ContentReader contentReader, OutputStream output) throws IOException {
				RemoteCallParameters remoteCallParameters = RemoteCallParameters.currentParameters();
				
				try (InputStream input = contentReader.getContentInputStream()) {
					if (remoteCallParameters.getContentRangeStart() == null && remoteCallParameters.getContentRangeEnd() == null) {
						IOUtils.copy(input, output);
					} else {
						long inputOffset = (remoteCallParameters.getContentRangeStart() != null) ? remoteCallParameters.getContentRangeStart() : 0L;
						long length = ((remoteCallParameters.getContentRangeEnd() != null) ? remoteCallParameters.getContentRangeEnd()+1 : Long.MAX_VALUE)
								 - inputOffset;
						IOUtils.copyLarge(input, output, inputOffset, length);
					}
				}
			}
		});
		
		NodeContentDeserializer<Void> defaultDeserializer = new NodeContentDeserializer<Void>() {
			@Override
			public Void deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
				NodeContentHolder holder = (NodeContentHolder) node.getContents().get(contentProperty);
				if (holder.getCallback() != null) {
					holder.getCallback().doWithInputStream(inputStream);
				}
				return null;
			}
		};
		serializationComponent = new NodeContentSerializationComponent(objectMapper, serializersByClass, defaultDeserializer);
	}
	
	public void setNodeService(NodeRemoteService nodeService) {
		this.nodeService = nodeService;
	}
	public void setRunAsEnabled(boolean runAsEnabled) {
		this.runAsEnabled = runAsEnabled;
	}

}
