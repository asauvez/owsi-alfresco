package fr.openwide.alfresco.api.core.node.exception;

import fr.openwide.alfresco.api.core.remote.exception.RepositoryRemoteException;

public class NodeExistsRemoteException extends RepositoryRemoteException {

	private static final long serialVersionUID = 6160581340364681804L;

	public NodeExistsRemoteException() {}

	public NodeExistsRemoteException(Throwable t) {
		super(t);
	}

}
