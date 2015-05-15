package fr.openwide.alfresco.api.core.node.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class RemoteCallParameters implements Serializable {

	private static final long serialVersionUID = 6488062940282466202L;

	private Integer compressionLevel;

	public Integer getCompressionLevel() {
		return compressionLevel;
	}
	public void setCompressionLevel(Integer compressionLevel) {
		this.compressionLevel = compressionLevel;
	}

}
