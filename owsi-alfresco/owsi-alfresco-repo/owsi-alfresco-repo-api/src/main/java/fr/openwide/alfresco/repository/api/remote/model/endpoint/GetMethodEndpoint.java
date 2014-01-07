package fr.openwide.alfresco.repository.api.remote.model.endpoint;

import org.springframework.http.HttpMethod;

public abstract class GetMethodEndpoint<R> extends RestEndpoint<R> {

	public GetMethodEndpoint(String path) {
		super(path, HttpMethod.GET);
	}

}
