package fr.openwide.alfresco.app.core.remote.service.impl;

import java.net.URI;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.ObjectArrays;

import fr.openwide.alfresco.app.core.authentication.model.RepositoryTicketAware;
import fr.openwide.alfresco.app.core.remote.model.RepositoryIOException;
import fr.openwide.alfresco.app.core.security.service.UserService;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;

public class RepositoryRemoteBinding {

	private static final Logger logger = LoggerFactory.getLogger(RepositoryRemoteBinding.class);

	private final RestTemplate restTemplate;
	private final UserService userService;
	private final String rootUri;
	private final String ticketParam;

	public RepositoryRemoteBinding(RestTemplate restTemplate, String rootUri) {
		this(restTemplate, rootUri, null);
	}

	public RepositoryRemoteBinding(RestTemplate restTemplate, String rootUri, String ticketParam) {
		this(restTemplate, rootUri, ticketParam, null);
	}

	public RepositoryRemoteBinding(RestTemplate restTemplate, String rootUri, String ticketParam, UserService userService) {
		this.restTemplate = restTemplate;
		this.rootUri = rootUri;
		this.ticketParam = ticketParam;
		this.userService = userService;
	}

	/**
	 * Correspond à l'utilisation générique de {@link RestTemplate#getForObject(String, Class, Object...)} ou
	 * {@link RestTemplate#put(String, Object, Object...)}
	 */
	public <T> T exchange(String path, HttpMethod method, Class<T> responseType, Object... urlVariables) throws RepositoryRemoteException {
		return exchange(path, method, null, responseType, urlVariables);
	}

	/**
	 * Correspond à l'utilisation générique de {@link RestTemplate#delete(String, Object...)}
	 */
	public void exchange(String path, HttpMethod method,  Object... urlVariables) throws RepositoryRemoteException {
		exchange(path, method, null, null, urlVariables);
	}

	/**
	 * Correspond à l'utilisation générique de {@link RestTemplate#postForObject(String, Object, Class, Object...)}
	 */
	public <T> T exchange(String path, HttpMethod method, Object request, Class<T> responseType, Object... urlVariables) throws RepositoryRemoteException {
		return exchange(path, method, request, responseType, null, urlVariables);
	}

	public <T> T exchange(String path, HttpMethod method, Object request, Class<T> responseType, HttpHeaders headers, Object... urlVariables) throws RepositoryRemoteException {
		URI uri = getURI(path, urlVariables);
		if (logger.isDebugEnabled()) {
			logger.debug("Executing " + method + " method to uri: " + uri);
		}
		try {
			ResponseEntity<T> exchange = restTemplate.exchange(uri, method, new HttpEntity<Object>(request, headers), responseType);
			return exchange.getBody();
		} catch (ResourceAccessException e) {
			// log to debug, target exception should be logged by the caller/framework
			if (logger.isDebugEnabled()) {
				logger.debug("Exception on " + method + " method to uri: " + uri, e);
			}
			throw mapResourceAccessException(e);
		} catch (Exception e) {
			// log to debug, target exception should be logged by the caller/framework
			if (logger.isDebugEnabled()) {
				logger.debug("Unexpected exception on " + method + " method to uri: " + uri, e);
			}
			throw new IllegalStateException(e);
		}
	}

	public <T> T exchangeCollection(String path, HttpMethod method,  Object request, ParameterizedTypeReference<T> responseType, Object... urlVariables) throws RepositoryRemoteException {
		return exchangeCollection(path, method, request, responseType, null, urlVariables);
	}

	public <T> T exchangeCollection(String path, HttpMethod method, Object request, ParameterizedTypeReference<T> responseType, HttpHeaders headers, Object... urlVariables) throws RepositoryRemoteException {
		URI uri = getURI(path, urlVariables);
		if (logger.isDebugEnabled()) {
			logger.debug("Executing " + method + " method to uri: " + uri);
		}
		try {
			ResponseEntity<T> exchange = restTemplate.exchange(uri, method, new HttpEntity<Object>(request, headers), responseType);
			return exchange.getBody();
		} catch (ResourceAccessException e) {
			// log to debug, target exception should be logged by the caller/framework
			if (logger.isDebugEnabled()) {
				logger.debug("Exception on " + method + " method to uri: " + uri, e);
			}
			throw mapResourceAccessException(e);
		} catch (Exception e) {
			// log to debug, target exception should be logged by the caller/framework
			if (logger.isDebugEnabled()) {
				logger.debug("Unexpected exception on " + method + " method to uri: " + uri, e);
			}
			throw new IllegalStateException(e);
		}
	}
	
	protected URI getURI(String path, Object... uriVars) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(rootUri).path(path);
		Object[] uriVariables = uriVars;
		if (ticketParam != null) {
			builder.queryParam(ticketParam, "{" + ticketParam + "}");
			if (userService != null) {
				// get ticket
				RepositoryTicketAware user = userService.getCurrentUser();
				uriVariables = ObjectArrays.concat(uriVariables, user.getTicket());
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
		if (e.getCause() instanceof RepositoryIOException) {
			return (RepositoryRemoteException) e.getCause().getCause();
		}
		return new RepositoryRemoteException(e);
	}

}
