package fr.openwide.alfresco.app.core.remote.model;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;

import com.fasterxml.jackson.databind.type.TypeFactory;

import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.repository.api.node.binding.NodePayloadCallback;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentDeserializer;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentSerializationComponent;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentSerializer;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.RestEndpoint;

public class NodeRestCallBuilder<R> extends RestCallBuilder<R> {

	private final RepositoryContentSerializationComponent serializationComponent;

	public NodeRestCallBuilder(RepositoryRemoteBinding repositoryRemoteBinding, RestEndpoint<R> restCall,
			RepositoryContentSerializationComponent serializationComponent) {
		super(repositoryRemoteBinding, restCall);
		this.serializationComponent = serializationComponent;
	}

	public R callPayloadSerializer(
			final Object payload, 
			final Collection<RepositoryNode> nodes, 
			final NodePayloadCallback<R> payloadCallback,
			final Map<NameReference, RepositoryContentSerializer<?>> serializers,
			final Map<NameReference, RepositoryContentDeserializer<?>> deserializers) {
		
		this.header("Content-Type", RepositoryContentSerializationComponent.CONTENT_TYPE);
		
		RequestCallback requestCallback = new RequestCallback() {
			@Override
			public void doWithRequest(ClientHttpRequest request) throws IOException {
				serializationComponent.serialize(
						payload, nodes, 
						serializers, 
						request.getBody());
			}
		};
		ResponseExtractor<R> responseExtractor = new ResponseExtractor<R>() {
			@Override
			public R extractData(ClientHttpResponse response) throws IOException {
				return serializationComponent.deserialize(
						TypeFactory.defaultInstance().constructType(getRestCallType()), 
						payloadCallback,
						deserializers, 
						response.getBody());
			}
		};
		
		return call(requestCallback, responseExtractor);
	}
}
