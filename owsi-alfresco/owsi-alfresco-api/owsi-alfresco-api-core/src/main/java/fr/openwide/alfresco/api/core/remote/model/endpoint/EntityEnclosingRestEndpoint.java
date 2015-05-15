package fr.openwide.alfresco.api.core.remote.model.endpoint;

import org.springframework.http.HttpMethod;

public abstract class EntityEnclosingRestEndpoint<R> extends RestEndpoint<R> {

	public EntityEnclosingRestEndpoint(String path, HttpMethod method) {
		super(path, method);
	}

}
