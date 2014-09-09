package fr.openwide.alfresco.repository.remote.framework.exception;

import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;

public class InvalidPayloadException extends RepositoryRemoteException {

	private static final long serialVersionUID = 2510765840876186680L;

	public InvalidPayloadException() {
		super();
	}

	public InvalidPayloadException(String message) {
		super(message);
	}

	public InvalidPayloadException(Throwable cause) {
		super(cause);
	}

	public InvalidPayloadException(String message, Throwable cause) {
		super(message, cause);
	}

}
