package fr.openwide.alfresco.app.core.remote.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.repository.wsgenerator.annotation.WebScriptEndPoint;
import fr.openwide.alfresco.repository.wsgenerator.model.WebScriptParam;

public class RepositoryRemoteCallBuilder<R> {

	private final RepositoryRemoteBinding repositoryRemoteBinding;

	private final HttpHeaders headers = new HttpHeaders();
	private final List<Object> urlVariables = new ArrayList<Object>();
	private final WebScriptParam<R> payload;
	private final WebScriptEndPoint endPoint;

	public RepositoryRemoteCallBuilder(RepositoryRemoteBinding repositoryRemoteBinding, WebScriptParam<R> payload) {
		this.repositoryRemoteBinding = repositoryRemoteBinding;
		this.payload = payload;
		this.endPoint = payload.getClass().getAnnotation(WebScriptEndPoint.class);
		if (endPoint == null) {
			throw new IllegalStateException(payload.getClass() + " should have a @WebScriptEndPoint annotation.");
		}
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
				return getRestCallType();
			}
		};
		return repositoryRemoteBinding.exchange(
				endPoint.url(), 
				endPoint.method(), 
				payload, 
				headers, 
				responseType, 
				urlVariables.toArray());
	}

	public R call(RequestCallback requestCallback, ResponseExtractor<R> responseExtractor) {
		return repositoryRemoteBinding.exchange(
				endPoint.url(), 
				endPoint.method(),
				headers, 
				requestCallback,
				responseExtractor, 
				urlVariables.toArray());
	}
	
	public WebScriptParam<R> getPayload() {
		return payload;
	}
	
	public Type getRestCallType() {
		Type type = payload.getClass().getGenericSuperclass();
		return ((ParameterizedType) type).getActualTypeArguments()[0];
	}
}
