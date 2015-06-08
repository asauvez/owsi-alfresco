package fr.openwide.alfresco.api.core.remote.model.endpoint;


public abstract class GetMethodEndpoint<R> extends RemoteEndpoint<R> {

	public GetMethodEndpoint(String path) {
		super(path, RemoteEndpointMethod.GET);
	}

}
