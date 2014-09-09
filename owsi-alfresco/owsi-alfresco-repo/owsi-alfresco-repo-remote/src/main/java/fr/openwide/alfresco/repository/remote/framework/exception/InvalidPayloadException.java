package fr.openwide.alfresco.repository.remote.framework.exception;

/**
 * Cette exception n'est visible que côté repository et n'a pas vocation à devenir une RepositoryRemoteException
 * Elle est exposée côté client sous forme d'une {@link fr.openwide.alfresco.repository.api.remote.exception.InvalidMessageRemoteException}
 */
public class InvalidPayloadException extends RuntimeException {

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