package fr.openwide.alfresco.app.core.remote.service.impl;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.ObjectArrays;

import fr.openwide.alfresco.app.core.authentication.model.RepositoryTicketProvider;
import fr.openwide.alfresco.app.core.remote.model.NodeRestCallBuilder;
import fr.openwide.alfresco.app.core.remote.model.RepositoryConnectException;
import fr.openwide.alfresco.app.core.remote.model.RepositoryIOException;
import fr.openwide.alfresco.app.core.remote.model.RestCallBuilder;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentSerializationComponent;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.EntityEnclosingRestEndpoint;
import fr.openwide.alfresco.repository.api.remote.model.endpoint.RestEndpoint;

public class RepositoryRemoteBinding {

	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryRemoteBinding.class);

	private final RestTemplate restTemplate;
	private final RepositoryContentSerializationComponent serializationComponent;
	private final Optional<RepositoryTicketProvider> ticketProvider;
	private final String rootUri;
	private final String ticketParam;
	private final String ticketHeader;

	public RepositoryRemoteBinding(RestTemplate restTemplate, RepositoryContentSerializationComponent serializationComponent,
			String rootUri) {
		this(restTemplate, serializationComponent, rootUri, null);
	}

	public RepositoryRemoteBinding(RestTemplate restTemplate, RepositoryContentSerializationComponent serializationComponent,
			String rootUri, String ticketParam) {
		this(restTemplate, serializationComponent, rootUri, ticketParam, null, null);
	}

	public RepositoryRemoteBinding(RestTemplate restTemplate, RepositoryContentSerializationComponent serializationComponent,
			String rootUri, String ticketParam, String ticketHeader, RepositoryTicketProvider ticketProvider) {
		this.restTemplate = restTemplate;
		this.serializationComponent = serializationComponent;
		this.rootUri = rootUri;
		this.ticketParam = ticketParam;
		this.ticketHeader = ticketHeader;
		this.ticketProvider = Optional.fromNullable(ticketProvider);
	}

	public <R> RestCallBuilder<R> builder(RestEndpoint<R> restCall) {
		return new RestCallBuilder<R>(this, restCall);
	}
	public <R> RestCallBuilder<R> builder(EntityEnclosingRestEndpoint<R> restCall, Object content) {
		return new RestCallBuilder<R>(this, restCall, content);
	}
	public <R> NodeRestCallBuilder<R> builderWithSerializer(EntityEnclosingRestEndpoint<R> restCall) {
		return new NodeRestCallBuilder<R>(this, restCall, serializationComponent);
	}

	public <T> T exchange(String path, HttpMethod method, Object request, HttpHeaders headers, ParameterizedTypeReference<T> responseType, Object... urlVariables) {
		URI uri = getURI(path, urlVariables);
		addTicketHeader(headers);
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(request, headers);
		return execute(uri, method, requestEntity, null, responseType, null);
	}

	public <T> T exchange(String path, HttpMethod method, final HttpHeaders headers, 
			final RequestCallback requestCallback, ResponseExtractor<T> responseExtractor, 
			Object... urlVariables) {
		URI uri = getURI(path, urlVariables);
		addTicketHeader(headers);
		RequestCallback realRequestCallback = new RequestCallback() {
			@Override
			public void doWithRequest(ClientHttpRequest request) throws IOException {
				request.getHeaders().putAll(headers);
				if (requestCallback != null) {
					requestCallback.doWithRequest(request);
				}
			}
		};
		return execute(uri, method, null, realRequestCallback, null, responseExtractor);
	}

	protected void addTicketHeader(HttpHeaders headers) {
		if (ticketHeader != null && ticketProvider.isPresent()) {
			// get ticket
			RepositoryTicket ticket = ticketProvider.get().getTicket();
			headers.add(ticketHeader, ticket.getTicket());
		}
	}

	protected <T> T execute(URI uri, HttpMethod method, HttpEntity<Object> requestEntity, RequestCallback requestCallback, 
			ParameterizedTypeReference<T> responseType, ResponseExtractor<T> responseExtractor) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing {} method with uri: {}", method, uri);
		}
		try {
			if (responseExtractor != null) {
				return restTemplate.execute(uri, method, requestCallback, responseExtractor);
			} else {
				ResponseEntity<T> exchange = restTemplate.exchange(uri, method, requestEntity, responseType);
				return exchange.getBody();
			}
		} catch (ResourceAccessException e) {
			// log to debug, target exception should be logged by the caller/framework
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Exception on " + method + " method with uri: " + uri, e);
			}
			throw mapResourceAccessException(e);
		} catch (Exception e) {
			// log to debug, target exception should be logged by the caller/framework
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Unexpected exception on " + method + " method with uri: " + uri, e);
			}
			throw new IllegalStateException(e);
		}
	}

	protected URI getURI(String path, Object... uriVars) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(rootUri).path(path);
		Object[] uriVariables = uriVars;
		if (ticketParam != null) {
			builder.queryParam(ticketParam, "{" + ticketParam + "}");
			if (ticketProvider.isPresent()) {
				// get ticket
				RepositoryTicket ticket = ticketProvider.get().getTicket();
				uriVariables = ObjectArrays.concat(uriVariables, ticket.getTicket());
			} else {
				// check ticket
				boolean contains = Iterables.any(Arrays.asList(uriVariables), new Predicate<Object>() {
					@Override
					public boolean apply(Object input) {
						return input instanceof RepositoryTicket;
					}
				});
				if (! contains) {
					throw new IllegalStateException("Ticket should be provided for param: " + ticketParam);
				}
			}
		}
		UriComponents uriComponents = builder.buildAndExpand(uriVariables);
		return uriComponents.toUri();
	}

	protected RepositoryRemoteException mapResourceAccessException(ResourceAccessException e) {
		if (e.getCause() instanceof ConnectException) {
			throw new RepositoryConnectException(e);
		}
		if (e.getCause() instanceof RepositoryIOException) {
			return (RepositoryRemoteException) e.getCause().getCause();
		}
		return new RepositoryRemoteException(e);
	}

}
