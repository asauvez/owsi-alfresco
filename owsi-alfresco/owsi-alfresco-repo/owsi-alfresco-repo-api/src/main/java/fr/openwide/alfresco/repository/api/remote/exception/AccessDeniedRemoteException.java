package fr.openwide.alfresco.repository.api.remote.exception;

public class AccessDeniedRemoteException extends RepositoryRemoteException {

	private static final long serialVersionUID = 5301662560426323353L;

	public AccessDeniedRemoteException() {
		super();
	}

	public AccessDeniedRemoteException(String msg) {
		super(msg);
	}

	public AccessDeniedRemoteException(Throwable cause) {
		super(cause);
	}

}
