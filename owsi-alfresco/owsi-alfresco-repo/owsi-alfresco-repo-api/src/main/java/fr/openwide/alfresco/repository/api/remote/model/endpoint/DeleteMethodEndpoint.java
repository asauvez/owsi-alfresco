package fr.openwide.alfresco.repository.api.remote.model.endpoint;

import org.springframework.http.HttpMethod;

public abstract class DeleteMethodEndpoint<R> extends RestEndpoint<R> {

	public DeleteMethodEndpoint(String path) {
		super(path, HttpMethod.DELETE);
	}

}
