package fr.openwide.alfresco.repository.api.remote.model.endpoint;

import org.springframework.http.HttpMethod;

public abstract class PostMethodEndpoint<R> extends EntityEnclosingRestEndpoint<R> {

	public PostMethodEndpoint(String path) {
		super(path, HttpMethod.POST);
	}

}
