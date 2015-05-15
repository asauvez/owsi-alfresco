package fr.openwide.alfresco.api.core.remote.model.endpoint;

import org.springframework.http.HttpMethod;

public abstract class GetMethodEndpoint<R> extends RemoteEndpoint<R> {

	public GetMethodEndpoint(String path) {
		super(path, HttpMethod.GET);
	}

}
