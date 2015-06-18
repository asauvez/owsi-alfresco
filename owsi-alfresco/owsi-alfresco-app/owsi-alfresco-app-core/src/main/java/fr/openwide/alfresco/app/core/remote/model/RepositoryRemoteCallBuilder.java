package fr.openwide.alfresco.app.core.remote.model;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.core.remote.model.endpoint.EntityEnclosingRemoteEndpoint;
import fr.openwide.alfresco.api.core.remote.model.endpoint.RemoteEndpoint;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;

public class RepositoryRemoteCallBuilder<R> {

	private final RepositoryRemoteBinding repositoryRemoteBinding;
	private final RemoteEndpoint<R> restCall;

	private HttpHeaders headers = new HttpHeaders();
	private List<Object> urlVariables = new ArrayList<Object>();
	private Object content;

	public RepositoryRemoteCallBuilder(RepositoryRemoteBinding repositoryRemoteBinding, RemoteEndpoint<R> restCall) {
		this.repositoryRemoteBinding = repositoryRemoteBinding;
		this.restCall = restCall;
	}

	public RepositoryRemoteCallBuilder(RepositoryRemoteBinding repositoryRemoteBinding, EntityEnclosingRemoteEndpoint<R> restCall, Object content) {
		this (repositoryRemoteBinding, restCall);
		this.content = content;
	}

	public RepositoryRemoteCallBuilder<R> header(String headerName, String headerValue) {
		headers.add(headerName, headerValue);
		return this;
	}

	public RepositoryRemoteCallBuilder<R> urlVariable(Object value) {
		urlVariables.add(value);
		return this;
	}

	public RepositoryRemoteCallBuilder<R> urlVariable(NodeReference nodeReference) {
		Matcher matcher = NodeReference.PATTERN.matcher(nodeReference.getReference());
		matcher.matches();
		urlVariable(matcher.group(NodeReference.PATTERN_WORKSPACE));
		urlVariable(matcher.group(NodeReference.PATTERN_STORE));
		urlVariable(matcher.group(NodeReference.PATTERN_UUID));
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

	public R call(RequestCallback requestCallback, ResponseExtractor<R> responseExtractor) {
		return repositoryRemoteBinding.exchange(
				restCall.getPath(), 
				restCall.getMethod(), 
				headers, 
				requestCallback,
				responseExtractor, 
				urlVariables.toArray());
	}
	
	public Type getRestCallType() {
		return restCall.getType();
	}
}
