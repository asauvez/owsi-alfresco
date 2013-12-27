package fr.openwide.alfresco.app.core.remote.service.impl;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;

import fr.openwide.alfresco.app.core.remote.model.RepositoryIOException;
import fr.openwide.alfresco.repository.api.remote.exception.AccessDeniedRemoteException;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;

public class RepositoryRemoteExceptionHandler extends DefaultResponseErrorHandler {

	private HttpMessageConverter<Object> messageConverter = new MappingJackson2HttpMessageConverter();

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {
		handleRemoteException(response);
		// fallback on default response error handler
		super.handleError(response);
	}

	private void handleRemoteException(ClientHttpResponse response) throws IOException {
		String exceptionClassName = response.getHeaders().getFirst(RepositoryRemoteException.HEADER_EXCEPTION_CLASS_NAME);
		if (exceptionClassName != null) {
			try {
				Class<?> clazz = Class.forName(exceptionClassName);
				if (RepositoryRemoteException.class.isAssignableFrom(clazz)) {
					// cast is safe because of test
					@SuppressWarnings("unchecked")
					Class<? extends RepositoryRemoteException> exceptionClass = (Class<? extends RepositoryRemoteException>) clazz;
					
					RepositoryRemoteException remoteException = (RepositoryRemoteException) messageConverter.read(exceptionClass, response);
					throw new RepositoryIOException(remoteException);
				}
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		} else if (HttpStatus.UNAUTHORIZED.equals(response.getStatusCode())) {
			throw new RepositoryIOException(new AccessDeniedRemoteException());
		}
	}

}
