package fr.openwide.alfresco.repository.api.remote.model.endpoint;

import org.springframework.http.HttpMethod;

public abstract class PutMethodEndpoint<R> extends EntityEnclosingRestEndpoint<R> {

	public PutMethodEndpoint(String path) {
		super(path, HttpMethod.PUT);
	}

}
