package fr.openwide.alfresco.app.core.remote.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.type.TypeFactory;

import fr.openwide.alfresco.app.core.node.binding.ByteArrayRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.binding.InputStreamRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.binding.MultipartFileRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.binding.StringRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.binding.TempFileRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.repository.api.node.binding.NodePayloadCallback;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentDeserializer;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentSerializationUtils;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentSerializer;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.EntityEnclosingRestEndpoint;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.RestEndpoint;

public class RestCallBuilder<R> {

	private static final TypeFactory TYPE_FACTORY = TypeFactory.defaultInstance();
	private static final RepositoryContentDeserializer<?> DEFAULT_REPOSITORY_CONTENT_DESERIALIZER = ByteArrayRepositoryContentSerializer.INSTANCE;
	private static final Map<Class<?>, RepositoryContentSerializer<?>> SERIALIZERS_BY_CLASS = new HashMap<>();
	static {
		SERIALIZERS_BY_CLASS.put(String.class, StringRepositoryContentSerializer.INSTANCE);
		SERIALIZERS_BY_CLASS.put(byte[].class, ByteArrayRepositoryContentSerializer.INSTANCE);
		SERIALIZERS_BY_CLASS.put(File.class, TempFileRepositoryContentSerializer.INSTANCE);
		SERIALIZERS_BY_CLASS.put(MultipartFile.class, MultipartFileRepositoryContentSerializer.INSTANCE);
		SERIALIZERS_BY_CLASS.put(InputStream.class, InputStreamRepositoryContentSerializer.INSTANCE);
	}
	
	private final RepositoryRemoteBinding repositoryRemoteBinding;
	private final RestEndpoint<R> restCall;

	private HttpHeaders headers = new HttpHeaders();
	private List<Object> urlVariables = new ArrayList<Object>();
	private Object content;

	public RestCallBuilder(RepositoryRemoteBinding repositoryRemoteBinding, RestEndpoint<R> restCall) {
		this.repositoryRemoteBinding = repositoryRemoteBinding;
		this.restCall = restCall;
	}

	public RestCallBuilder(RepositoryRemoteBinding repositoryRemoteBinding, EntityEnclosingRestEndpoint<R> restCall, Object content) {
		this (repositoryRemoteBinding, restCall);
		this.content = content;
	}

	public RestCallBuilder<R> header(String headerName, String headerValue) {
		headers.add(headerName, headerValue);
		return this;
	}

	public RestCallBuilder<R> urlVariable(Object value) {
		urlVariables.add(value);
		return this;
	}

	public RestCallBuilder<R> urlVariable(NodeReference nodeReference) {
		Matcher matcher = NodeReference.PATTERN.matcher(nodeReference.getReference());
		matcher.matches();
		urlVariable(matcher.group(1));
		urlVariable(matcher.group(2));
		urlVariable(matcher.group(3));
		return this;
	}
	
	public R call() {
		ParameterizedTypeReference<R> responseType = new ParameterizedTypeReference<R>() {
			@Override
			public Type getType() {
				return restCall.getType();
			}
		};
		return repositoryRemoteBinding.exchange(
				restCall.getPath(), 
				restCall.getMethod(), 
				content, 
				headers, 
				responseType, 
				urlVariables.toArray());
	}

	public R call(ResponseExtractor<R> responseExtractor) {
		return repositoryRemoteBinding.exchange(
				restCall.getPath(), 
				restCall.getMethod(), 
				headers, 
				null,
				responseExtractor, 
				urlVariables.toArray());
	}
	
	public R callPayloadSerializer(
			final Object payload, 
			final Collection<RepositoryNode> nodes, 
			final NodePayloadCallback<R> payloadCallback,
			final Map<NameReference, RepositoryContentSerializer<?>> serializers,
			final Map<NameReference, RepositoryContentDeserializer<?>> deserializers) {
		
		this.header("Content-Type", RepositoryContentSerializationUtils.CONTENT_TYPE);
		
		RequestCallback requestCallback = new RequestCallback() {
			@Override
			public void doWithRequest(ClientHttpRequest request) throws IOException {
				RepositoryContentSerializationUtils.serialize(
						payload, nodes, 
						serializers, 
						SERIALIZERS_BY_CLASS, 
						request.getBody());
			}
		};
		ResponseExtractor<R> responseExtractor = new ResponseExtractor<R>() {
			@Override
			public R extractData(ClientHttpResponse response) throws IOException {
				return RepositoryContentSerializationUtils.deserialize(
						TYPE_FACTORY.constructType(restCall.getType()), 
						payloadCallback,
						deserializers, 
						DEFAULT_REPOSITORY_CONTENT_DESERIALIZER, 
						response.getBody());
			}
		};
		
		return repositoryRemoteBinding.exchange(
				restCall.getPath(), 
				restCall.getMethod(), 
				headers, 
				requestCallback,
				responseExtractor, 
				urlVariables.toArray());
	}
}
