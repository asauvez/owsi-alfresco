package fr.openwide.alfresco.repository.api.remote.model;

public class IllegalStateRemoteException extends RepositoryRemoteException {

	private static final long serialVersionUID = -2439683842480984283L;

	public IllegalStateRemoteException() {
		super();
	}

	public IllegalStateRemoteException(String message) {
		super(message);
	}

	public IllegalStateRemoteException(Throwable cause) {
		super(cause);
	}

	public IllegalStateRemoteException(String message, Throwable cause) {
		super(message, cause);
	}

}
