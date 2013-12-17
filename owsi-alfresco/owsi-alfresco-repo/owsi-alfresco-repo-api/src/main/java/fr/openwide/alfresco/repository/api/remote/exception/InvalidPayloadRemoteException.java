package fr.openwide.alfresco.repository.api.remote.exception;

public class InvalidPayloadRemoteException extends RepositoryRemoteException {

	public static final String HEADER_PAYLOAD = "X-Payload";

	private static final long serialVersionUID = 7676769912186329111L;

	public InvalidPayloadRemoteException() {
		super();
	}

	public InvalidPayloadRemoteException(String message) {
		super(message);
	}

	public InvalidPayloadRemoteException(Throwable cause) {
		super(cause);
	}

	public InvalidPayloadRemoteException(String message, Throwable cause) {
		super(message, cause);
	}

}
