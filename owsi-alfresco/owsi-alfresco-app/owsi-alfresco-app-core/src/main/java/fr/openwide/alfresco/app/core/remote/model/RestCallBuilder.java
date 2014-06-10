package fr.openwide.alfresco.app.core.remote.model;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.ResponseExtractor;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.repository.api.remote.exception.InvalidMessageRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.EntityEnclosingRestEndpoint;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.RestEndpoint;


public class RestCallBuilder<R> {

	private static MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
	
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

	public RestCallBuilder<R> headerPayload(Object payload) {
		try {
			String value = messageConverter.getObjectMapper().writeValueAsString(payload);
			return header(RestEndpoint.HEADER_MESSAGE_CONTENT, URLEncoder.encode(value, "UTF-8"));
		} catch (JsonProcessingException | UnsupportedEncodingException e) {
			throw new InvalidMessageRemoteException(e);
		}
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
				responseType, 
				headers, 
				urlVariables.toArray());
	}

	public void call(ResponseExtractor<?> responseExtractor) {
		repositoryRemoteBinding.getRequestContent(
				restCall.getPath(), 
				restCall.getMethod(),  
				responseExtractor, 
				urlVariables.toArray());
	}
}
