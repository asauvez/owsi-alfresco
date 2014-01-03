package fr.openwide.alfresco.repository.api.node.exception;

import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;

public class DuplicateChildNameException extends RepositoryRemoteException {

	private static final long serialVersionUID = 6160581340364681804L;

	public DuplicateChildNameException() {}
	
	public DuplicateChildNameException(Throwable cause) {
		super(cause);
	}

}
