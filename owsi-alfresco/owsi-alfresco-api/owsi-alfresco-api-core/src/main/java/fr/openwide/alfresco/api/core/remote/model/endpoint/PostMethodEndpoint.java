package fr.openwide.alfresco.api.core.remote.model.endpoint;

public abstract class PostMethodEndpoint<R> extends EntityEnclosingRemoteEndpoint<R> {

	public PostMethodEndpoint(String path) {
		super(path, RemoteEndpointMethod.POST);
	}

}
