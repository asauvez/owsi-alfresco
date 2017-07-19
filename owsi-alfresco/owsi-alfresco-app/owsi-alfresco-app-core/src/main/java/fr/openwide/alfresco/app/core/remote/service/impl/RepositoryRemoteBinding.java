package fr.openwide.alfresco.app.core.remote.service.impl;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;

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

import java.util.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.ObjectArrays;

import fr.openwide.alfresco.api.core.authentication.model.TicketReference;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentDeserializationParameters;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializationComponent;
import fr.openwide.alfresco.api.core.node.binding.content.NodeContentSerializationParameters;
import fr.openwide.alfresco.api.core.node.binding.content.NodePayloadCallback;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.api.core.remote.exception.UnauthorizedRemoteException;
import fr.openwide.alfresco.app.core.remote.model.RepositoryConnectException;
import fr.openwide.alfresco.app.core.remote.model.RepositoryIOException;
import fr.openwide.alfresco.app.core.remote.model.RepositoryNodeRemoteCallBuilder;
import fr.openwide.alfresco.app.core.remote.model.RepositoryRemoteCallBuilder;
import fr.openwide.alfresco.app.core.security.service.TicketReferenceProvider;
import fr.openwide.alfresco.repository.wsgenerator.annotation.GenerateWebScript.WebScriptMethod;
import fr.openwide.alfresco.repository.wsgenerator.model.WebScriptParam;

public class RepositoryRemoteBinding {

	private static final Logger LOGGER = LoggerFactory.getLogger(RepositoryRemoteBinding.class);
	private static final Logger LOGGER_AUDIT = LoggerFactory.getLogger(RepositoryRemoteBinding.class.getName() + "_audit");

	private final RestTemplate restTemplate;
	private final NodeContentSerializationComponent serializationComponent;
	private final Optional<TicketReferenceProvider> ticketProvider;
	private final String rootUri;
	private final String ticketParam;
	private final String ticketHeader;

	public RepositoryRemoteBinding(RestTemplate restTemplate, NodeContentSerializationComponent serializationComponent,
			String rootUri) {
		this(restTemplate, serializationComponent, rootUri, null);
	}

	public RepositoryRemoteBinding(RestTemplate restTemplate, NodeContentSerializationComponent serializationComponent,
			String rootUri, String ticketParam) {
		this(restTemplate, serializationComponent, rootUri, ticketParam, null, null);
	}

	public RepositoryRemoteBinding(RestTemplate restTemplate, NodeContentSerializationComponent serializationComponent,
			String rootUri, String ticketParam, String ticketHeader, TicketReferenceProvider ticketProvider) {
		this.restTemplate = restTemplate;
		this.serializationComponent = serializationComponent;
		this.rootUri = rootUri;
		this.ticketParam = ticketParam;
		this.ticketHeader = ticketHeader;
		this.ticketProvider = Optional.ofNullable(ticketProvider);
	}

	public <R> RepositoryRemoteCallBuilder<R> builder(WebScriptParam<R> content) {
		return new RepositoryRemoteCallBuilder<R>(this, content);
	}
	public <R> RepositoryNodeRemoteCallBuilder<R> builderWithSerializer(WebScriptParam<R> payload) {
		return new RepositoryNodeRemoteCallBuilder<R>(this, serializationComponent, payload);
	}

	public <R, P extends WebScriptParam<R>> R callPayloadSerializer(
			final P payload, 
			final Collection<RepositoryNode> nodes, 
			final NodePayloadCallback<R> payloadCallback,
			final NodeContentSerializationParameters serializationParameters,
			final NodeContentDeserializationParameters deserializationParameters) {
		RepositoryNodeRemoteCallBuilder<R> remoteCallBuilder = builderWithSerializer(payload);
		return remoteCallBuilder.callPayloadSerializer(nodes, payloadCallback, serializationParameters, deserializationParameters);
	}
	
	public <T> T exchange(String path, WebScriptMethod method, Object request, HttpHeaders headers, ParameterizedTypeReference<T> responseType, Object... urlVariables) {
		URI uri = getURI(path, urlVariables);
		addTicketHeader(headers);
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(request, headers);
		return execute(uri, method, requestEntity, null, responseType, null);
	}

	public <T> T exchange(String path, WebScriptMethod method, final HttpHeaders headers, 
			final RequestCallback requestCallback, ResponseExtractor<T> responseExtractor, 
			Object... urlVariables) {
		RequestCallback realRequestCallback = new RequestCallback() {
			@Override
			public void doWithRequest(ClientHttpRequest request) throws IOException {
				request.getHeaders().putAll(headers);
				if (requestCallback != null) {
					requestCallback.doWithRequest(request);
				}
			}
		};
		
		for (int essai=0; ; essai++) {
			try {
				URI uri = getURI(path, urlVariables);
				addTicketHeader(headers);
				return execute(uri, method, null, realRequestCallback, null, responseExtractor);
			} catch (UnauthorizedRemoteException e) {
				if (ticketProvider.isPresent() && essai < 2) {
					ticketProvider.get().renewTicket();
				} else {
					throw e;
				}
			}
		}
	}

	private void addTicketHeader(HttpHeaders headers) {
		if (ticketHeader != null && ticketProvider.isPresent()) {
			// get ticket
			TicketReference ticket = ticketProvider.get().getTicket();
			headers.add(ticketHeader, ticket.getTicket());
		}
	}

	private <T> T execute(URI uri, WebScriptMethod method, HttpEntity<Object> requestEntity, RequestCallback requestCallback, 
			ParameterizedTypeReference<T> responseType, ResponseExtractor<T> responseExtractor) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Executing {} method with uri: {}", method, getProtectedURI(uri));
		}
		long before = System.currentTimeMillis();
		HttpMethod httpMethod = HttpMethod.valueOf(method.name());
		try {
			if (responseExtractor != null) {
				return restTemplate.execute(uri, httpMethod, requestCallback, responseExtractor);
			} else {
				ResponseEntity<T> exchange = restTemplate.exchange(uri, httpMethod, requestEntity, responseType);
				return exchange.getBody();
			}
		} catch (ResourceAccessException e) {
			// log to debug, target exception should be logged by the caller/framework
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Exception on " + method + " method with uri: " + getProtectedURI(uri), e);
			}
			throw mapResourceAccessException(e);
		} catch (Exception e) {
			// log to debug, target exception should be logged by the caller/framework
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Unexpected exception on " + method + " method with uri: " + getProtectedURI(uri), e);
			}
			throw new IllegalStateException(e);
		} finally {
			if (LOGGER_AUDIT.isDebugEnabled()) {
				LOGGER_AUDIT.debug("{} : {} ms", getProtectedURI(uri), System.currentTimeMillis() - before);
			}
		}
	}
	
	private URI getProtectedURI(URI uri) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri);
		return (builder.build().getQueryParams().get(ticketParam) != null) ? builder.replaceQueryParam(ticketParam, "[PROTECTED]").build().toUri() : uri;
	}

	protected URI getURI(String path, Object... uriVariables) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(rootUri).path(path);
		if (ticketParam != null) {
			builder.queryParam(ticketParam, "{" + ticketParam + "}");
			if (ticketProvider.isPresent()) {
				// get ticket
				TicketReference ticket = ticketProvider.get().getTicket();
				uriVariables = ObjectArrays.concat(uriVariables, ticket.getTicket());
			} else {
				// check ticket
				boolean contains = Iterables.any(Arrays.asList(uriVariables), new Predicate<Object>() {
					@Override
					public boolean apply(Object input) {
						return input instanceof TicketReference;
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
