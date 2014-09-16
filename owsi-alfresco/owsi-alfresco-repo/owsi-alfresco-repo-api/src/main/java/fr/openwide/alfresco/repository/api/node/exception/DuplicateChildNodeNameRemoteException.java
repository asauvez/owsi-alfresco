package fr.openwide.alfresco.repository.api.node.exception;

import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;

public class DuplicateChildNodeNameRemoteException extends RepositoryRemoteException {

	private static final long serialVersionUID = 6160581340364681804L;

	public DuplicateChildNodeNameRemoteException() {}
	
	public DuplicateChildNodeNameRemoteException(String name, Throwable cause) {
		super(name, cause);
	}

}
