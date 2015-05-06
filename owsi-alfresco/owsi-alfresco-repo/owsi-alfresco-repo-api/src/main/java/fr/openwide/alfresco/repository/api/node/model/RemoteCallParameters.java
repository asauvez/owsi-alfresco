package fr.openwide.alfresco.repository.api.node.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class RemoteCallParameters implements Serializable {

	private Integer compressionLevel = null;
	
	public Integer getCompressionLevel() {
		return compressionLevel;
	}
	public void setCompressionLevel(Integer compressionLevel) {
		this.compressionLevel = compressionLevel;
	}
}
