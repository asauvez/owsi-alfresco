package fr.openwide.alfresco.query.repo.exception;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "cause", "stackTrace", "localizedMessage" })
public class AbstractRemoteException extends Exception {

	private static final long serialVersionUID = -1282988112820849307L;

	public static final String HEADER_EXCEPTION_CLASS_NAME = "X-Remote-Exception-ClassName";

	public AbstractRemoteException() {
		super();
	}

	public AbstractRemoteException(String message, Throwable cause) {
		super(message, cause);
	}

	public AbstractRemoteException(String message) {
		super(message);
	}

	public AbstractRemoteException(Throwable cause) {
		super(cause);
	}

}
