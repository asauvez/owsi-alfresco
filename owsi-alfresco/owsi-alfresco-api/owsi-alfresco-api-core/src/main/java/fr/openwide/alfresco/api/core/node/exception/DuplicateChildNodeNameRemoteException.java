package fr.openwide.alfresco.api.core.node.exception;

import fr.openwide.alfresco.api.core.remote.exception.RepositoryRemoteException;

public class DuplicateChildNodeNameRemoteException extends RepositoryRemoteException {

	private static final long serialVersionUID = 6160581340364681804L;

	public DuplicateChildNodeNameRemoteException() {}
	
	public DuplicateChildNodeNameRemoteException(String name, Throwable cause) {
		super(name, cause);
	}

}
