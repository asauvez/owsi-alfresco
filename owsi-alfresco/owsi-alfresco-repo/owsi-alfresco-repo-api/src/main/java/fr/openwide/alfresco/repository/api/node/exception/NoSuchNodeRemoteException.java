package fr.openwide.alfresco.repository.api.node.exception;

import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;

public class NoSuchNodeRemoteException extends RepositoryRemoteException {

	private static final long serialVersionUID = 6160581340364681804L;

	public NoSuchNodeRemoteException() {}

	public NoSuchNodeRemoteException(String msg) {
		super(msg);
	}
	
}
