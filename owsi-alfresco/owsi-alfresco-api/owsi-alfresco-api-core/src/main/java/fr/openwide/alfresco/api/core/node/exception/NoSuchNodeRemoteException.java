package fr.openwide.alfresco.api.core.node.exception;

import fr.openwide.alfresco.api.core.remote.exception.RepositoryRemoteException;

public class NoSuchNodeRemoteException extends RepositoryRemoteException {

	private static final long serialVersionUID = 6160581340364681804L;

	public NoSuchNodeRemoteException() {}

	public NoSuchNodeRemoteException(String msg) {
		super(msg);
	}
	public NoSuchNodeRemoteException(Exception e) {
		super(e);
	}
	
}
