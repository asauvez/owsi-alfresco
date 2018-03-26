package fr.openwide.alfresco.api.core.search.model.highlight;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class RepositoryHighlightResult implements Serializable {
	
	private final NameReference field;
	private List<String> extracts;
	
	@JsonCreator
	public RepositoryHighlightResult(
			@JsonProperty("field") NameReference field, 
			@JsonProperty("extracts") List<String> extracts) {
		super();
		this.field = field;
		this.extracts = extracts;
	}
	
	public NameReference getField() {
		return field;
	}
	public List<String> getExtracts() {
		return extracts;
	}
}
