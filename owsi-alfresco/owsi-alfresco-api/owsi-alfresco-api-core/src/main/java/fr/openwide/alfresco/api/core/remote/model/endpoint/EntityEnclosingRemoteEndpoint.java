package fr.openwide.alfresco.api.core.remote.model.endpoint;


public abstract class EntityEnclosingRemoteEndpoint<R> extends RemoteEndpoint<R> {

	public EntityEnclosingRemoteEndpoint(String path, RemoteEndpointMethod method) {
		super(path, method);
	}

}
