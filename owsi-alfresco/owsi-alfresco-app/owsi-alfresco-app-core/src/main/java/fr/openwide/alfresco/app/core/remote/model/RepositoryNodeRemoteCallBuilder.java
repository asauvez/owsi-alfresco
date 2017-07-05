package fr.openwide.alfresco.app.core.remote.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;

import com.fasterxml.jackson.databind.type.TypeFactory;

import fr.openwide.alfresco.api.core.node.binding.content.NodeContentDeserializationParameters;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializationComponent;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializationParameters;
import fr.openwide.alfresco.api.core.node.binding.content.NodePayloadCallback;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.repository.wsgenerator.model.WebScriptParam;

public class RepositoryNodeRemoteCallBuilder<R> extends RepositoryRemoteCallBuilder<R> {

	private final NodeContentSerializationComponent serializationComponent;

	public RepositoryNodeRemoteCallBuilder(
			RepositoryRemoteBinding repositoryRemoteBinding, 
			NodeContentSerializationComponent serializationComponent,
			WebScriptParam<R> payload) {
		super(repositoryRemoteBinding, payload);
		this.serializationComponent = serializationComponent;
	}

	public R callPayloadSerializer() {
		return callPayloadSerializer(null, null, null, null);
	}
	
			
	public R callPayloadSerializer(
			final Collection<RepositoryNode> nodes, 
			final NodePayloadCallback<R> payloadCallback,
			final NodeContentSerializationParameters serializationParameters,
			final NodeContentDeserializationParameters deserializationParameters) {
		
		this.header("Content-Type", NodeContentSerializationComponent.CONTENT_TYPE);
		
		RequestCallback requestCallback = new RequestCallback() {
			@Override
			public void doWithRequest(ClientHttpRequest request) throws IOException {
				try (OutputStream outputStream = request.getBody()) {
					serializationComponent.serialize(
							getPayload(), nodes, 
							serializationParameters, 
							outputStream);
				}
			}
		};
		ResponseExtractor<R> responseExtractor = new ResponseExtractor<R>() {
			@Override
			public R extractData(ClientHttpResponse response) throws IOException {
				try (InputStream inputStream = response.getBody()) {
					return serializationComponent.deserialize(
							TypeFactory.defaultInstance().constructType(getRestCallType()), 
							payloadCallback,
							deserializationParameters, 
							inputStream).getPayload();
				}
			}
		};
		
		return call(requestCallback, responseExtractor);
	}
}
