package fr.openwide.alfresco.api.core.remote.model.endpoint;

import org.springframework.http.HttpMethod;

public abstract class PutMethodEndpoint<R> extends EntityEnclosingRemoteEndpoint<R> {

	public PutMethodEndpoint(String path) {
		super(path, HttpMethod.PUT);
	}

}
