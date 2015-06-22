package fr.openwide.alfresco.api.core.search.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class RepositorySortDefinition implements Serializable {
	
	private final NameReference property;
	private final boolean ascending;

	@JsonCreator
	public RepositorySortDefinition(
			@JsonProperty("property") NameReference property, 
			@JsonProperty("ascending") boolean ascending) {
		this.property = property;
		this.ascending = ascending;
	}

	public NameReference getProperty() {
		return property;
	}

	public boolean isAscending() {
		return ascending;
	}
	
}
