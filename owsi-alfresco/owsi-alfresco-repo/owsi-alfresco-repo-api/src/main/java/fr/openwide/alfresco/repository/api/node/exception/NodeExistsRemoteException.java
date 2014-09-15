package fr.openwide.alfresco.repository.api.node.exception;

import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;

public class NodeExistsRemoteException extends RepositoryRemoteException {

	private static final long serialVersionUID = 6160581340364681804L;

	public NodeExistsRemoteException() {}

	public NodeExistsRemoteException(Throwable t) {
		super(t);
	}

}
