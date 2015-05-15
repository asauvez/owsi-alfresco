package fr.openwide.alfresco.api.core.remote.model.endpoint;

import org.springframework.http.HttpMethod;

public abstract class EntityEnclosingRemoteEndpoint<R> extends RemoteEndpoint<R> {

	public EntityEnclosingRemoteEndpoint(String path, HttpMethod method) {
		super(path, method);
	}

}
