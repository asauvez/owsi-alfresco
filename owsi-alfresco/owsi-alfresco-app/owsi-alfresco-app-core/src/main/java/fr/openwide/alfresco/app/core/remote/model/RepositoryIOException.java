package fr.openwide.alfresco.app.core.remote.model;

import java.io.IOException;

public class RepositoryIOException extends IOException {

	private static final long serialVersionUID = -5194807875938830619L;

	public RepositoryIOException() {
		super();
	}

	public RepositoryIOException(String message) {
		super(message);
	}

	public RepositoryIOException(Throwable cause) {
		super(cause);
	}

	public RepositoryIOException(String message, Throwable cause) {
		super(message, cause);
	}

}
