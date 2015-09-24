package fr.openwide.alfresco.api.core.remote.model.endpoint;


public abstract class PutMethodEndpoint<R> extends EntityEnclosingRemoteEndpoint<R> {

	public PutMethodEndpoint(String path) {
		super(path, RemoteEndpointMethod.PUT);
	}

}
