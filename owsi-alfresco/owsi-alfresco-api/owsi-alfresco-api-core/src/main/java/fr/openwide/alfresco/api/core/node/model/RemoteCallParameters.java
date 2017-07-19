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
	private Long contentRangeStart = null;
	private Long contentRangeEnd = null;

	public int getCompressionLevel() {
		return compressionLevel;
	}
	protected void setCompressionLevel(int compressionLevel) { // pour JSon
		this.compressionLevel = compressionLevel;
	}
	public RemoteCallParameters compressionLevel(int compressionLevel) {
		this.compressionLevel = compressionLevel;
		return this;
	}
	
	public UserReference getRunAs() {
		return runAs;
	}
	protected void setRunAs(UserReference runAs) { // pour JSon
		this.runAs = runAs;
	}
	public RemoteCallParameters runAs(UserReference runAs) {
		this.runAs = runAs;
		return this;
	}
	
	public Long getContentRangeStart() {
		return contentRangeStart;
	}
	public RemoteCallParameters contentRangeStart(Long contentRangeStart) {
		this.contentRangeStart = contentRangeStart;
		return this;
	}
	protected void setContentRangeStart(Long contentRangeStart) { // pour JSon
		this.contentRangeStart = contentRangeStart;
	}
	public Long getContentRangeEnd() {
		return contentRangeEnd;
	}
	public RemoteCallParameters contentRangeEnd(Long contentRangeEnd) {
		this.contentRangeEnd = contentRangeEnd;
		return this;
	}
	protected void setContentRangeEnd(Long contentRangeEnd) { // pour JSon
		this.contentRangeEnd = contentRangeEnd;
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
