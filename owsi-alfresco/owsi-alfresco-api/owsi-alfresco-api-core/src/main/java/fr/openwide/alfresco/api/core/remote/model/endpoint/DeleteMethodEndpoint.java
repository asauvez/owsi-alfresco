package fr.openwide.alfresco.api.core.remote.model.endpoint;

import org.springframework.http.HttpMethod;

public abstract class DeleteMethodEndpoint<R> extends RestEndpoint<R> {

	public DeleteMethodEndpoint(String path) {
		super(path, HttpMethod.DELETE);
	}

}
