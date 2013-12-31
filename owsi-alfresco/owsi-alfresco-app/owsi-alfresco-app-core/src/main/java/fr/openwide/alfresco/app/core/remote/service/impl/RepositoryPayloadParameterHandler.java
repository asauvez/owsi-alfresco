package fr.openwide.alfresco.app.core.remote.service.impl;

import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import com.fasterxml.jackson.core.JsonProcessingException;

import fr.openwide.alfresco.repository.api.remote.exception.InvalidMessageRemoteException;

public class RepositoryPayloadParameterHandler {

	private MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();

	public HttpHeaders handlePayload(Object payload) throws InvalidMessageRemoteException {
		HttpHeaders headers = new HttpHeaders();
		String value;
		try {
			value = messageConverter.getObjectMapper().writeValueAsString(payload);
		} catch (JsonProcessingException e) {
			throw new InvalidMessageRemoteException(e);
		}
		headers.add(InvalidMessageRemoteException.HEADER_MESSAGE_CONTENT, value);
		return headers;
	}

}
