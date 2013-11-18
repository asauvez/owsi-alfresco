package fr.openwide.alfresco.query.repo.exception;


public class RemoteUnknownException extends AbstractRemoteException {

	private static final long serialVersionUID = -634286982170659357L;

	public RemoteUnknownException() {
		super();
	}

	public RemoteUnknownException(String message, Throwable cause) {
		super(message, cause);
	}

	public RemoteUnknownException(String message) {
		super(message);
	}

	public RemoteUnknownException(Throwable cause) {
		super(cause);
	}

}
