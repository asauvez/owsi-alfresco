package fr.openwide.alfresco.repository.api.remote.exception;

public class InvalidMessageRemoteException extends RepositoryRemoteException {

	public static final String HEADER_MESSAGE_CONTENT = "X-Remote-Message-Content";

	private static final long serialVersionUID = 7676769912186329111L;

	public InvalidMessageRemoteException() {
		super();
	}

	public InvalidMessageRemoteException(String message) {
		super(message);
	}

	public InvalidMessageRemoteException(Throwable cause) {
		super(cause);
	}

	public InvalidMessageRemoteException(String message, Throwable cause) {
		super(message, cause);
	}

}
