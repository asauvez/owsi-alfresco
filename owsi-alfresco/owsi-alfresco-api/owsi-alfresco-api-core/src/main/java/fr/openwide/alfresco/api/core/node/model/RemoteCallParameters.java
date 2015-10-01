package fr.openwide.alfresco.api.core.node.model;

import java.io.Serializable;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import fr.openwide.alfresco.api.core.authentication.model.UserReference;

@JsonInclude(Include.NON_EMPTY)
public class RemoteCallParameters implements Serializable {

	private static final RemoteCallParameters DEFAULT_INSTANCE = new RemoteCallParameters();
	
	private static final int DEFAULT_COMPRESSION_LEVEL = 0; // sans compression
	private static final ThreadLocal<RemoteCallParameters> CURRENT = new ThreadLocal<>();

	private int compressionLevel = DEFAULT_COMPRESSION_LEVEL;
	private UserReference runAs = null;

	public int getCompressionLevel() {
		return compressionLevel;
	}
	public RemoteCallParameters compressionLevel(int compressionLevel) {
		this.compressionLevel = compressionLevel;
		return this;
	}
	
	public UserReference getRunAs() {
		return runAs;
	}
	public RemoteCallParameters runAs(UserReference runAs) {
		this.runAs = runAs;
		return this;
	}

	public static RemoteCallParameters currentParameters() {
		RemoteCallParameters parameters = CURRENT.get();
		return (parameters != null) ? parameters : DEFAULT_INSTANCE;
	}
	
	public static <V> V execute(RemoteCallParameters parameters, Callable<V> callable) {
		RemoteCallParameters oldValue = CURRENT.get();
		try {
			CURRENT.set(parameters);
			return callable.call();
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} finally {
			if (oldValue != null) {
				CURRENT.set(oldValue);
			} else {
				CURRENT.remove();
			}
		}
	}
}
