package fr.openwide.alfresco.api.core.search.model.highlight;

import java.io.Serializable;
import java.util.List;

import org.alfresco.service.namespace.QName;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RepositoryHighlightResult implements Serializable {
	
	private final QName field;
	private List<String> extracts;
	
	@JsonCreator
	public RepositoryHighlightResult(
			@JsonProperty("field") QName field, 
			@JsonProperty("extracts") List<String> extracts) {
		super();
		this.field = field;
		this.extracts = extracts;
	}
	
	public QName getField() {
		return field;
	}
	public List<String> getExtracts() {
		return extracts;
	}
}
