package fr.openwide.alfresco.repository.api.remote.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Checked exception, forcing caller to catch and deal with it or its children
 * In case there is nothing obvious to do, re-throw a runtime exception or a checked exception that will rollback the
 * main transaction and display a user-friendly message
 */
@JsonIgnoreProperties(value = { "cause", "stackTrace", "suppressed", "localizedMessage" })
public class RepositoryRemoteException extends Exception {

	public static final String HEADER_EXCEPTION_CLASS_NAME = "X-Exception-ClassName";

	private static final long serialVersionUID = 3755181707213263899L;

	public RepositoryRemoteException() {
		super();
	}

	public RepositoryRemoteException(String message) {
		super(message);
	}

	public RepositoryRemoteException(Throwable cause) {
		super(cause);
	}

	public RepositoryRemoteException(String message, Throwable cause) {
		super(message, cause);
	}

}
