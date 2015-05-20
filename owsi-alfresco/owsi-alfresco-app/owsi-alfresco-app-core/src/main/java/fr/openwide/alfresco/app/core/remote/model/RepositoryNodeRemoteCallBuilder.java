package fr.openwide.alfresco.app.core.remote.model;

import java.io.IOException;
import java.util.Collection;

import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;

import com.fasterxml.jackson.databind.type.TypeFactory;

import fr.openwide.alfresco.api.core.node.binding.NodeContentDeserializationParameters;
import fr.openwide.alfresco.api.core.node.binding.NodeContentSerializationComponent;
import fr.openwide.alfresco.api.core.node.binding.NodeContentSerializationParameters;
import fr.openwide.alfresco.api.core.node.binding.NodePayloadCallback;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.endpoint.RemoteEndpoint;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;

public class RepositoryNodeRemoteCallBuilder<R> extends RepositoryRemoteCallBuilder<R> {

	private final NodeContentSerializationComponent serializationComponent;

	public RepositoryNodeRemoteCallBuilder(RepositoryRemoteBinding repositoryRemoteBinding, RemoteEndpoint<R> restCall,
			NodeContentSerializationComponent serializationComponent) {
		super(repositoryRemoteBinding, restCall);
		this.serializationComponent = serializationComponent;
	}

	public R callPayloadSerializer(Object payload) {
		return callPayloadSerializer(payload, null, null, null, null);
	}
	
			
	public R callPayloadSerializer(
			final Object payload, 
			final Collection<RepositoryNode> nodes, 
			final NodePayloadCallback<R> payloadCallback,
			final NodeContentSerializationParameters serializationParameters,
			final NodeContentDeserializationParameters deserializationParameters) {
		
		this.header("Content-Type", NodeContentSerializationComponent.CONTENT_TYPE);
		
		RequestCallback requestCallback = new RequestCallback() {
			@Override
			public void doWithRequest(ClientHttpRequest request) throws IOException {
				serializationComponent.serialize(
						payload, nodes, 
						serializationParameters, 
						request.getBody());
			}
		};
		ResponseExtractor<R> responseExtractor = new ResponseExtractor<R>() {
			@Override
			public R extractData(ClientHttpResponse response) throws IOException {
				return serializationComponent.deserialize(
						TypeFactory.defaultInstance().constructType(getRestCallType()), 
						payloadCallback,
						deserializationParameters, 
						response.getBody()).getPayload();
			}
		};
		
		return call(requestCallback, responseExtractor);
	}
}
