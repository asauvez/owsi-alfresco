package fr.openwide.alfresco.repository.api.remote.model.endpoint;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.springframework.http.HttpMethod;

public abstract class RestEndpoint<R> {

	private String path;
	private HttpMethod method;
	private Type parameterType;
	
	public RestEndpoint(String path, HttpMethod method) {
		this.path = path;
		this.method = method;
		
		Type type = getClass().getGenericSuperclass();
		this.parameterType = ((ParameterizedType) type).getActualTypeArguments()[0];
	}
	
	public String getPath() {
		return path;
	}
	public HttpMethod getMethod() {
		return method;
	}
	public Type getType() {
		return parameterType;
	}
}
