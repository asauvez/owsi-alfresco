package fr.openwide.alfresco.api.core.remote.model.endpoint;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class RemoteEndpoint<R> {

	public static enum RemoteEndpointMethod {
		GET, PUT, POST, DELETE, HEAD, OPTIONS;
	}

	
	private String path;
	private RemoteEndpointMethod method;
	private Type parameterType;

	public RemoteEndpoint(String path, RemoteEndpointMethod method) {
		this.path = path;
		this.method = method;
		Type type = getClass().getGenericSuperclass();
		this.parameterType = ((ParameterizedType) type).getActualTypeArguments()[0];
	}
	
	public String getPath() {
		return path;
	}

	public RemoteEndpointMethod getMethod() {
		return method;
	}

	public Type getType() {
		return parameterType;
	}

}
