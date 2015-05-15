package fr.openwide.alfresco.api.core.remote.model.endpoint;

import org.springframework.http.HttpMethod;

public abstract class PostMethodEndpoint<R> extends EntityEnclosingRestEndpoint<R> {

	public PostMethodEndpoint(String path) {
		super(path, HttpMethod.POST);
	}

}
