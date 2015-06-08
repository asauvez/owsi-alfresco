package fr.openwide.alfresco.api.core.remote.model.endpoint;


public abstract class DeleteMethodEndpoint<R> extends RemoteEndpoint<R> {

	public DeleteMethodEndpoint(String path) {
		super(path, RemoteEndpointMethod.DELETE);
	}

}
