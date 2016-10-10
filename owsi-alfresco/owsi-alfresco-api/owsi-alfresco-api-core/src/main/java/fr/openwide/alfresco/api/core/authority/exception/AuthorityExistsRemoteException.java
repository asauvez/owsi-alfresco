package fr.openwide.alfresco.api.core.authority.exception;

import fr.openwide.alfresco.api.core.remote.exception.RepositoryRemoteException;

public class AuthorityExistsRemoteException extends RepositoryRemoteException {
	private static final long serialVersionUID = -2994625034697499465L;

	public AuthorityExistsRemoteException() {
		super();
	}
	public AuthorityExistsRemoteException(Throwable t) {
		super(t);
	}
}
